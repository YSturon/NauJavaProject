package ru.sturov.naujava.report.service;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.AtomicReference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.sturov.naujava.auth.repository.AppUserRepository;
import ru.sturov.naujava.report.dto.CategoryReportRow;
import ru.sturov.naujava.report.dto.ReportContentView;
import ru.sturov.naujava.report.entity.Report;
import ru.sturov.naujava.report.entity.ReportStatus;
import ru.sturov.naujava.report.repository.ReportRepository;
import ru.sturov.naujava.repository.CategoryRepository;

@Service
public class ReportServiceImpl implements ReportService {

    private static final Logger log = LoggerFactory.getLogger(ReportServiceImpl.class);

    private final ReportRepository reportRepository;
    private final AppUserRepository appUserRepository;
    private final CategoryRepository categoryRepository;
    private final ReportTemplateRenderer reportTemplateRenderer;

    public ReportServiceImpl(
            ReportRepository reportRepository,
            AppUserRepository appUserRepository,
            CategoryRepository categoryRepository,
            ReportTemplateRenderer reportTemplateRenderer) {
        this.reportRepository = reportRepository;
        this.appUserRepository = appUserRepository;
        this.categoryRepository = categoryRepository;
        this.reportTemplateRenderer = reportTemplateRenderer;
    }

    @Override
    @Transactional
    public Long createReport() {
        Report report = new Report();
        report.setStatus(ReportStatus.CREATED);
        report = reportRepository.save(report);

        report.setContent(reportTemplateRenderer.renderPendingReport(report.getId()));
        reportRepository.save(report);

        return report.getId();
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<ReportContentView> getReportContent(Long reportId) {
        return reportRepository.findById(reportId).map(this::toContentView);
    }

    @Override
    @Async("reportTaskExecutor")
    public CompletableFuture<Void> generateReportAsync(Long reportId) {
        if (reportRepository.findById(reportId).isEmpty()) {
            log.warn("Report {} not found. Generation skipped.", reportId);
            return CompletableFuture.completedFuture(null);
        }

        log.info("Starting report generation for reportId={}", reportId);

        long totalStartTime = System.currentTimeMillis();
        AtomicLong userCount = new AtomicLong();
        AtomicLong userCountElapsed = new AtomicLong();
        AtomicReference<List<CategoryReportRow>> categoriesRef = new AtomicReference<>(List.of());
        AtomicLong categoriesElapsed = new AtomicLong();
        AtomicReference<Throwable> failureRef = new AtomicReference<>();

        Thread userCountThread = new Thread(
                () -> executeUserCountTask(userCount, userCountElapsed, failureRef), "report-user-count-" + reportId);

        Thread categoryThread = new Thread(
                () -> executeCategoryTask(categoriesRef, categoriesElapsed, failureRef),
                "report-categories-" + reportId);

        try {
            userCountThread.start();
            categoryThread.start();

            userCountThread.join();
            categoryThread.join();

            Throwable failure = failureRef.get();
            if (failure != null) {
                String message = buildFailureMessage(failure);
                markReportAsError(reportId, message);
                log.error("Report generation failed for reportId={}", reportId, failure);
                return CompletableFuture.completedFuture(null);
            }

            long totalElapsed = System.currentTimeMillis() - totalStartTime;
            String html = reportTemplateRenderer.renderCompletedReport(
                    reportId,
                    userCount.get(),
                    userCountElapsed.get(),
                    categoriesRef.get(),
                    categoriesElapsed.get(),
                    totalElapsed);

            updateReport(reportId, ReportStatus.COMPLETED, html);
            log.info("Report generation completed for reportId={}", reportId);

            return CompletableFuture.completedFuture(null);
        } catch (InterruptedException ex) {
            Thread.currentThread().interrupt();
            markReportAsError(reportId, "Формирование отчета было прервано");
            log.warn("Report generation interrupted for reportId={}", reportId, ex);
            return CompletableFuture.completedFuture(null);
        } catch (Exception ex) {
            markReportAsError(reportId, buildFailureMessage(ex));
            log.error("Unexpected error during report generation for reportId={}", reportId, ex);
            return CompletableFuture.completedFuture(null);
        }
    }

    private ReportContentView toContentView(Report report) {
        String content = report.getContent();

        if (content == null || content.isBlank()) {
            content = switch (report.getStatus()) {
                case CREATED -> reportTemplateRenderer.renderPendingReport(report.getId());
                case COMPLETED ->
                    reportTemplateRenderer.renderErrorReport(report.getId(), "Содержимое готового отчета отсутствует");
                case ERROR -> reportTemplateRenderer.renderErrorReport(report.getId(), "Отчет завершился с ошибкой");
            };
        }

        return new ReportContentView(report.getStatus(), content);
    }

    private void executeUserCountTask(
            AtomicLong userCount, AtomicLong userCountElapsed, AtomicReference<Throwable> failureRef) {
        long startTime = System.currentTimeMillis();
        try {
            userCount.set(appUserRepository.count());
        } catch (Throwable ex) {
            failureRef.compareAndSet(null, ex);
        } finally {
            userCountElapsed.set(System.currentTimeMillis() - startTime);
        }
    }

    private void executeCategoryTask(
            AtomicReference<List<CategoryReportRow>> categoriesRef,
            AtomicLong categoriesElapsed,
            AtomicReference<Throwable> failureRef) {
        long startTime = System.currentTimeMillis();
        try {
            List<CategoryReportRow> categories = categoryRepository.findAllByOrderByIdAsc().stream()
                    .map(CategoryReportRow::fromEntity)
                    .toList();
            categoriesRef.set(categories);
        } catch (Throwable ex) {
            failureRef.compareAndSet(null, ex);
        } finally {
            categoriesElapsed.set(System.currentTimeMillis() - startTime);
        }
    }

    private void updateReport(Long reportId, ReportStatus status, String content) {
        reportRepository
                .findById(reportId)
                .ifPresentOrElse(
                        report -> {
                            report.setStatus(status);
                            report.setContent(content);
                            reportRepository.save(report);
                        },
                        () -> log.warn("Report {} not found while trying to update status to {}", reportId, status));
    }

    private void markReportAsError(Long reportId, String errorMessage) {
        updateReport(reportId, ReportStatus.ERROR, reportTemplateRenderer.renderErrorReport(reportId, errorMessage));
    }

    private String buildFailureMessage(Throwable throwable) {
        String message = throwable.getMessage();
        if (message == null || message.isBlank()) {
            return "Не удалось сформировать отчет: " + throwable.getClass().getSimpleName();
        }
        return "Не удалось сформировать отчет: " + message;
    }
}
