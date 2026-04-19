package ru.sturov.naujava.controller.rest;

import java.net.URI;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.sturov.naujava.report.dto.ReportContentView;
import ru.sturov.naujava.report.dto.ReportCreationResponse;
import ru.sturov.naujava.report.entity.ReportStatus;
import ru.sturov.naujava.report.service.ReportService;
import ru.sturov.naujava.report.service.ReportTemplateRenderer;

/** REST-контроллер запуска и получения HTML-отчетов. */
@RestController
@RequestMapping("/api/reports")
public class ReportRestController {

    private static final MediaType HTML_UTF8 = MediaType.parseMediaType("text/html;charset=UTF-8");

    private final ReportService reportService;
    private final ReportTemplateRenderer reportTemplateRenderer;

    public ReportRestController(ReportService reportService, ReportTemplateRenderer reportTemplateRenderer) {
        this.reportService = reportService;
        this.reportTemplateRenderer = reportTemplateRenderer;
    }

    @PostMapping
    public ResponseEntity<ReportCreationResponse> createReport() {
        Long reportId = reportService.createReport();
        reportService.generateReportAsync(reportId);

        String contentUrl = "/api/reports/" + reportId;
        ReportCreationResponse response = new ReportCreationResponse(reportId, ReportStatus.CREATED, contentUrl);

        return ResponseEntity.accepted().location(URI.create(contentUrl)).body(response);
    }

    @GetMapping("/{reportId}")
    public ResponseEntity<String> getReportContent(@PathVariable Long reportId) {
        return reportService
                .getReportContent(reportId)
                .map(this::toHtmlResponse)
                .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .contentType(HTML_UTF8)
                        .body(reportTemplateRenderer.renderNotFoundReport(reportId)));
    }

    private ResponseEntity<String> toHtmlResponse(ReportContentView contentView) {
        HttpStatus status =
                switch (contentView.status()) {
                    case CREATED -> HttpStatus.ACCEPTED;
                    case COMPLETED -> HttpStatus.OK;
                    case ERROR -> HttpStatus.CONFLICT;
                };

        return ResponseEntity.status(status).contentType(HTML_UTF8).body(contentView.htmlContent());
    }
}
