package uz.uzkassa.cts.repository;

import org.springframework.stereotype.Repository;
import uz.uzkassa.cts.domain.hrms.VacancyApplicationTemplate;
import uz.uzkassa.cts.repository.custom.VacancyApplicationTemplateRepositoryCustom;

@Repository
public interface VacancyApplicationTemplateRepository extends BaseRepository<VacancyApplicationTemplate, Long>, VacancyApplicationTemplateRepositoryCustom {
}
