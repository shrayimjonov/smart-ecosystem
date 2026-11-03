package uz.uzkassa.cts.repository.custom;

import org.springframework.stereotype.Repository;
import uz.uzkassa.cts.domain.hrms.SalaryAllocation;
import uz.uzkassa.cts.dto.base.ResultList;
import uz.uzkassa.cts.filter.SalaryAllocationFilter;

@Repository
public interface SalaryAllocationRepositoryCustom {

    ResultList<SalaryAllocation> getResultList(SalaryAllocationFilter filter);
}
