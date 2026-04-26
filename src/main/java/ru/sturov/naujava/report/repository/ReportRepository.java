package ru.sturov.naujava.report.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import ru.sturov.naujava.report.entity.Report;

/** Репозиторий хранения отчетов. */
@RepositoryRestResource(exported = false)
public interface ReportRepository extends CrudRepository<Report, Long> {}
