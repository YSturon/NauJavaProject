package ru.sturov.naujava.repository;

import java.util.List;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import ru.sturov.naujava.entity.Question;
import ru.sturov.naujava.repository.custom.QuestionCriteriaRepository;

/**
 * Основной репозиторий для работы с сущностью {@link Question}.
 *
 * <p>Закрывает пункт задания с обычными Spring Data запросами:
 *
 * <ul>
 *   <li>{@link #findByDifficultyLevelAndCategoryName(Integer, String)} - Query Method;
 *   <li>{@link #findByQuizTitle(String)} - JPQL через связанную сущность.
 * </ul>
 */
public interface QuestionRepository
        extends CrudRepository<Question, Long>, QuestionCriteriaRepository {

    /**
     * Ищет вопросы по уровню сложности и названию категории.
     *
     * @param difficultyLevel уровень сложности вопроса
     * @param categoryName название категории
     * @return список подходящих вопросов
     */
    List<Question> findByDifficultyLevelAndCategoryName(Integer difficultyLevel, String categoryName);

    /**
     * Ищет вопросы по названию теста через связанную сущность {@code quiz}.
     *
     * @param quizTitle название теста
     * @return список вопросов, относящихся к тесту
     */
    @Query("""
            select q
            from Question q
            where q.quiz.title = :quizTitle
            """)
    List<Question> findByQuizTitle(@Param("quizTitle") String quizTitle);
}
