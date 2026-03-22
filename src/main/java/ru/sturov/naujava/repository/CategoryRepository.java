package ru.sturov.naujava.repository;

import org.springframework.data.repository.CrudRepository;
import ru.sturov.naujava.entity.Category;

public interface CategoryRepository extends CrudRepository<Category, Long> {
}