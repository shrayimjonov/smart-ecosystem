package uz.uzkassa.cts.repository.custom;

import org.springframework.stereotype.Repository;
import uz.uzkassa.cts.domain.cases.Cases;
import uz.uzkassa.cts.dto.base.ResultList;
import uz.uzkassa.cts.filter.CaseFilter;

@Repository
public interface CaseRepositoryCustom {

    ResultList<Cases> getResultList(CaseFilter filter);
}
