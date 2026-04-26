package ru.sturov.naujava.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import ru.sturov.naujava.entity.Quiz;

@RepositoryRestResource(path = "quizzes")
public interface QuizRepository extends CrudRepository<Quiz, Long> {}
