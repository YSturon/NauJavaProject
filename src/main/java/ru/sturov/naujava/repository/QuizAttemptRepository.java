package ru.sturov.naujava.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import ru.sturov.naujava.entity.QuizAttempt;

@RepositoryRestResource(path = "quiz-attempts")
public interface QuizAttemptRepository extends CrudRepository<QuizAttempt, Long> {}
