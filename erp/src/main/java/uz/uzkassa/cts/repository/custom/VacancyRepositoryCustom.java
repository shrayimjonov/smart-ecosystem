package uz.uzkassa.cts.repository.custom;

import org.springframework.stereotype.Repository;
import uz.uzkassa.cts.domain.hrms.Vacancy;
import uz.uzkassa.cts.dto.base.ResultList;
import uz.uzkassa.cts.filter.VacancyFilter;

@Repository
public interface VacancyRepositoryCustom {

    ResultList<Vacancy> getResultList(VacancyFilter filter);
}
