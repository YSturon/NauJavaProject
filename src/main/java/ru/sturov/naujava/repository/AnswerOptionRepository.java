package ru.sturov.naujava.repository;

import org.springframework.data.repository.CrudRepository;
import ru.sturov.naujava.entity.AnswerOption;

public interface AnswerOptionRepository extends CrudRepository<AnswerOption, Long> {
}