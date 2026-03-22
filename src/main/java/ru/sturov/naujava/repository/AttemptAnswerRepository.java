package ru.sturov.naujava.repository;

import org.springframework.data.repository.CrudRepository;
import ru.sturov.naujava.entity.AttemptAnswer;

public interface AttemptAnswerRepository extends CrudRepository<AttemptAnswer, Long> {
}