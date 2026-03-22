package ru.sturov.naujava.repository;

import org.springframework.data.repository.CrudRepository;
import ru.sturov.naujava.entity.Quiz;

public interface QuizRepository extends CrudRepository<Quiz, Long> {
}