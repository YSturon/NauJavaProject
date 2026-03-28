package ru.sturov.naujava.controller.rest;

import java.util.List;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.sturov.naujava.dto.QuestionListItemResponse;
import ru.sturov.naujava.repository.QuestionRepository;

/** REST-контроллер для ручного доступа к кастомным запросам репозитория вопросов. */
@RestController
@RequestMapping("/api/custom/questions")
public class QuestionCustomRestController {

    private final QuestionRepository questionRepository;

    public QuestionCustomRestController(QuestionRepository questionRepository) {
        this.questionRepository = questionRepository;
    }
    /**
     * Ищет вопросы по уровню сложности и названию категории.
     *
     * @param difficultyLevel уровень сложности
     * @param categoryName название категории
     * @return список найденных вопросов
     */
    @GetMapping("/by-difficulty-and-category")
    public List<QuestionListItemResponse> findByDifficultyAndCategory(
            @RequestParam Integer difficultyLevel, @RequestParam String categoryName) {
        return questionRepository.findByDifficultyLevelAndCategoryName(difficultyLevel, categoryName).stream()
                .map(QuestionListItemResponse::fromEntity)
                .toList();
    }
    /**
     * Ищет вопросы по названию теста.
     *
     * @param quizTitle название теста
     * @return список найденных вопросов
     */
    @GetMapping("/by-quiz-title")
    public List<QuestionListItemResponse> findByQuizTitle(@RequestParam String quizTitle) {
        return questionRepository.findByQuizTitle(quizTitle).stream()
                .map(QuestionListItemResponse::fromEntity)
                .toList();
    }
}
