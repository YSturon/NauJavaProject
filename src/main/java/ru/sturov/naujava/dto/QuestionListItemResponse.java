package ru.sturov.naujava.dto;

import ru.sturov.naujava.entity.Question;

public record QuestionListItemResponse(Long id, String text, Integer difficultyLevel, String explanation) {

    public static QuestionListItemResponse fromEntity(Question question) {
        return new QuestionListItemResponse(
                question.getId(), question.getText(), question.getDifficultyLevel(), question.getExplanation());
    }
}
