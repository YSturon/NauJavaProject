package ru.sturov.naujava.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import ru.sturov.naujava.entity.AnswerOption;

@RepositoryRestResource(path = "answer-options")
public interface AnswerOptionRepository extends CrudRepository<AnswerOption, Long> {}
