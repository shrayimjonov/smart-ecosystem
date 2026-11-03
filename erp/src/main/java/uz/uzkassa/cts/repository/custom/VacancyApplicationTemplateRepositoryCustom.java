package uz.uzkassa.cts.repository.custom;

import org.springframework.stereotype.Repository;
import uz.uzkassa.cts.domain.hrms.VacancyApplicationTemplate;
import uz.uzkassa.cts.dto.base.ResultList;
import uz.uzkassa.cts.filter.BaseFilter;
import uz.uzkassa.cts.filter.VacancyApplicationTemplateFilter;

@Repository
public interface VacancyApplicationTemplateRepositoryCustom {

    ResultList<VacancyApplicationTemplate> getResultList(VacancyApplicationTemplateFilter filter);
}
