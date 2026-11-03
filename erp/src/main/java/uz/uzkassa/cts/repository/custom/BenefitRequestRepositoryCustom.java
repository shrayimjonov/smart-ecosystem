package uz.uzkassa.cts.repository.custom;

import uz.uzkassa.cts.domain.hrms.BenefitRequest;
import uz.uzkassa.cts.dto.base.ResultList;
import uz.uzkassa.cts.filter.BenefitRequestFilter;

public interface BenefitRequestRepositoryCustom {

    ResultList<BenefitRequest> getResultList(BenefitRequestFilter filter);
}
