package ru.sturov.naujava.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import ru.sturov.naujava.entity.AttemptAnswer;

@RepositoryRestResource(path = "attempt-answers")
public interface AttemptAnswerRepository extends CrudRepository<AttemptAnswer, Long> {}
