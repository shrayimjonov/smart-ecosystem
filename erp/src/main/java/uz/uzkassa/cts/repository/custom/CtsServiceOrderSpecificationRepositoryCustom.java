package uz.uzkassa.cts.repository.custom;

import uz.uzkassa.cts.domain.CtsServiceOrderSpecification;
import uz.uzkassa.cts.dto.base.ResultList;
import uz.uzkassa.cts.filter.BaseFilter;

public interface CtsServiceOrderSpecificationRepositoryCustom {

    ResultList<CtsServiceOrderSpecification> getResultList(BaseFilter filter);
}
