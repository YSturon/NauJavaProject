package ru.sturov.naujava.repository;

import org.springframework.data.repository.CrudRepository;
import ru.sturov.naujava.entity.QuizAttempt;

public interface QuizAttemptRepository extends CrudRepository<QuizAttempt, Long> {
}