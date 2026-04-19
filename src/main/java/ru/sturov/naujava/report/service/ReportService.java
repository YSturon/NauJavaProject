package ru.sturov.naujava.report.service;

import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import ru.sturov.naujava.report.dto.ReportContentView;

public interface ReportService {
    Long createReport();

    Optional<ReportContentView> getReportContent(Long reportId);

    CompletableFuture<Void> generateReportAsync(Long reportId);
}
