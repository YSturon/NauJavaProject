package ru.sturov.naujava.report.dto;

import ru.sturov.naujava.entity.Category;

public record CategoryReportRow(Long id, String name, String description, Boolean active) {

    public static CategoryReportRow fromEntity(Category category) {
        return new CategoryReportRow(
                category.getId(), category.getName(), category.getDescription(), category.getActive());
    }

    public String activeLabel() {
        return Boolean.TRUE.equals(active) ? "Да" : "Нет";
    }
}
