package ru.sturov.naujava.repository.custom;

import java.util.List;
import ru.sturov.naujava.entity.Question;

/**
 * Интерфейс репозитория для реализации запросов через Criteria API.
 *
 * <p>Содержит аналоги запросов из {@link ru.sturov.naujava.repository.QuestionRepository}, но построенные программно
 * через JPA Criteria API.
 */
public interface QuestionCriteriaRepository {

    /**
     * Аналог Query Method для поиска по уровню сложности и названию категории.
     *
     * @param difficultyLevel уровень сложности вопроса
     * @param categoryName название категории
     * @return список подходящих вопросов
     */
    List<Question> searchByDifficultyLevelAndCategoryNameUsingCriteria(Integer difficultyLevel, String categoryName);

    /**
     * Аналог JPQL-запроса для поиска по названию теста.
     *
     * @param quizTitle название теста
     * @return список вопросов, относящихся к тесту
     */
    List<Question> searchByQuizTitleUsingCriteria(String quizTitle);
}
