package ru.sturov.naujava.report.dto;

import ru.sturov.naujava.report.entity.ReportStatus;

public record ReportContentView(ReportStatus status, String htmlContent) {}
