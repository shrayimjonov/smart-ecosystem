package uz.uzkassa.cts.repository.custom;

import org.springframework.stereotype.Repository;
import uz.uzkassa.cts.domain.Lead;
import uz.uzkassa.cts.dto.base.ResultList;
import uz.uzkassa.cts.filter.LeadFilter;

@Repository
public interface LeadRepositoryCustom {

    ResultList<Lead> getResultList(LeadFilter filter);
}
