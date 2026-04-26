package ru.sturov.naujava.report.dto;

import ru.sturov.naujava.report.entity.ReportStatus;

public record ReportCreationResponse(Long reportId, ReportStatus status, String contentUrl) {}
