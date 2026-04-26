/**
 * Основной пакет приложения практической работы 4.
 *
 * <p>Реализованные пункты задания:
 *
 * <ul>
 *   <li>JPA-сущности предметной области: {@link ru.sturov.naujava.entity.Category},
 *       {@link ru.sturov.naujava.entity.Quiz}, {@link ru.sturov.naujava.entity.Question},
 *       {@link ru.sturov.naujava.entity.AnswerOption}, {@link ru.sturov.naujava.entity.QuizAttempt},
 *       {@link ru.sturov.naujava.entity.AttemptAnswer}.
 *   <li>Репозитории Spring Data: {@link ru.sturov.naujava.repository.CategoryRepository},
 *       {@link ru.sturov.naujava.repository.QuizRepository}, {@link ru.sturov.naujava.repository.QuestionRepository},
 *       {@link ru.sturov.naujava.repository.AnswerOptionRepository},
 *       {@link ru.sturov.naujava.repository.QuizAttemptRepository},
 *       {@link ru.sturov.naujava.repository.AttemptAnswerRepository}.
 *   <li>Query Method:
 *       {@link ru.sturov.naujava.repository.QuestionRepository#findByDifficultyLevelAndCategoryName(Integer, String)}.
 *   <li>JPQL-запрос через связанную сущность:
 *       {@link ru.sturov.naujava.repository.QuestionRepository#findByQuizTitle(String)}.
 *   <li>Аналогичные запросы через Criteria API:
 *       {@link ru.sturov.naujava.repository.custom.QuestionCriteriaRepository#searchByDifficultyLevelAndCategoryNameUsingCriteria(Integer,
 *       String)} и
 *       {@link ru.sturov.naujava.repository.custom.QuestionCriteriaRepository#searchByQuizTitleUsingCriteria(String)}.
 *   <li>Транзакционный сервис: {@link ru.sturov.naujava.service.QuizAttemptService} и
 *       {@link ru.sturov.naujava.service.QuizAttemptServiceImpl}.
 * </ul>
 */
package ru.sturov.naujava;
