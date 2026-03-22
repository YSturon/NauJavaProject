package ru.sturov.naujava.repository.impl;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import java.util.List;
import org.springframework.stereotype.Repository;
import ru.sturov.naujava.entity.Category;
import ru.sturov.naujava.entity.Question;
import ru.sturov.naujava.entity.Quiz;
import ru.sturov.naujava.repository.custom.QuestionCriteriaRepository;

@Repository
public class QuestionCriteriaRepositoryImpl implements QuestionCriteriaRepository {

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public List<Question> searchByDifficultyLevelAndCategoryNameUsingCriteria(
            Integer difficultyLevel, String categoryName) {
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<Question> cq = cb.createQuery(Question.class);
        Root<Question> root = cq.from(Question.class);
        Join<Question, Category> categoryJoin = root.join("category");

        Predicate difficultyPredicate = cb.equal(root.get("difficultyLevel"), difficultyLevel);
        Predicate categoryPredicate = cb.equal(categoryJoin.get("name"), categoryName);

        cq.select(root).where(cb.and(difficultyPredicate, categoryPredicate));

        return entityManager.createQuery(cq).getResultList();
    }

    @Override
    public List<Question> searchByQuizTitleUsingCriteria(String quizTitle) {
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<Question> cq = cb.createQuery(Question.class);
        Root<Question> root = cq.from(Question.class);
        Join<Question, Quiz> quizJoin = root.join("quiz");

        Predicate quizTitlePredicate = cb.equal(quizJoin.get("title"), quizTitle);
        cq.select(root).where(quizTitlePredicate);

        return entityManager.createQuery(cq).getResultList();
    }
}
