package uz.uzkassa.cts.repository.custom;

import org.springframework.stereotype.Repository;
import uz.uzkassa.cts.domain.hrms.LeaveRequestReason;
import uz.uzkassa.cts.dto.base.ResultList;
import uz.uzkassa.cts.filter.LeaveRequestReasonFilter;

@Repository
public interface LeaveRequestReasonRepositoryCustom {

    ResultList<LeaveRequestReason> getResultList(LeaveRequestReasonFilter filter);
}
