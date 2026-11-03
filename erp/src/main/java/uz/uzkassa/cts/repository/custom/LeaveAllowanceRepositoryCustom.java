package uz.uzkassa.cts.repository.custom;

import org.springframework.stereotype.Repository;
import uz.uzkassa.cts.domain.hrms.LeaveAllowance;
import uz.uzkassa.cts.dto.base.ResultList;
import uz.uzkassa.cts.filter.LeaveAllowanceFilter;

@Repository
public interface LeaveAllowanceRepositoryCustom {

    ResultList<LeaveAllowance> getResultList(LeaveAllowanceFilter filter);
}
