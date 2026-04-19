package ru.sturov.naujava.report.service;

import java.util.List;
import java.util.Locale;
import org.springframework.stereotype.Component;
import org.thymeleaf.ITemplateEngine;
import org.thymeleaf.context.Context;
import ru.sturov.naujava.report.dto.CategoryReportRow;

@Component
public class ReportTemplateRenderer {

    private static final String TEMPLATE_NAME = "reports/report-page";

    private final ITemplateEngine templateEngine;

    public ReportTemplateRenderer(ITemplateEngine templateEngine) {
        this.templateEngine = templateEngine;
    }

    public String renderPendingReport(Long reportId) {
        Context context = baseContext("Отчет формируется", reportId, "CREATED", "badge-created");
        context.setVariable("pending", true);
        return templateEngine.process(TEMPLATE_NAME, context);
    }

    public String renderCompletedReport(
            Long reportId,
            long registeredUsers,
            long usersElapsedMs,
            List<CategoryReportRow> categories,
            long categoriesElapsedMs,
            long totalElapsedMs) {
        Context context = baseContext("Отчет приложения", reportId, "COMPLETED", "badge-completed");
        context.setVariable("completed", true);
        context.setVariable("registeredUsers", registeredUsers);
        context.setVariable("usersElapsedMs", usersElapsedMs);
        context.setVariable("categories", categories);
        context.setVariable("categoriesElapsedMs", categoriesElapsedMs);
        context.setVariable("totalElapsedMs", totalElapsedMs);
        return templateEngine.process(TEMPLATE_NAME, context);
    }

    public String renderErrorReport(Long reportId, String errorMessage) {
        Context context = baseContext("Ошибка формирования отчета", reportId, "ERROR", "badge-error");
        context.setVariable("error", true);
        context.setVariable("errorMessage", errorMessage);
        return templateEngine.process(TEMPLATE_NAME, context);
    }

    public String renderNotFoundReport(Long reportId) {
        Context context = baseContext("Отчет не найден", reportId, "NOT_FOUND", "badge-error");
        context.setVariable("missing", true);
        context.setVariable("errorMessage", "Отчет с ID " + reportId + " отсутствует в системе.");
        return templateEngine.process(TEMPLATE_NAME, context);
    }

    private Context baseContext(String pageTitle, Long reportId, String statusName, String statusCssClass) {
        Context context = new Context(Locale.forLanguageTag("ru"));
        context.setVariable("pageTitle", pageTitle);
        context.setVariable("reportId", reportId);
        context.setVariable("statusName", statusName);
        context.setVariable("statusCssClass", statusCssClass);
        context.setVariable("pending", false);
        context.setVariable("completed", false);
        context.setVariable("error", false);
        context.setVariable("missing", false);
        context.setVariable("registeredUsers", 0L);
        context.setVariable("usersElapsedMs", 0L);
        context.setVariable("categoriesElapsedMs", 0L);
        context.setVariable("totalElapsedMs", 0L);
        context.setVariable("categories", List.of());
        context.setVariable("errorMessage", null);
        return context;
    }
}
