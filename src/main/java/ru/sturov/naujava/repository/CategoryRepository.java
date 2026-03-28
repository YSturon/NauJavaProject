package ru.sturov.naujava.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import ru.sturov.naujava.entity.Category;

@RepositoryRestResource(path = "categories")
public interface CategoryRepository extends CrudRepository<Category, Long> {}
