package uz.uzkassa.cts.repository.custom;

import org.springframework.stereotype.Repository;
import uz.uzkassa.cts.domain.regionalassignment.RegionalAssignment;
import uz.uzkassa.cts.dto.base.ResultList;
import uz.uzkassa.cts.filter.RegionalAssignmentFilter;

@Repository
public interface RegionalAssignmentRepositoryCustom {

    ResultList<RegionalAssignment> getResultList(RegionalAssignmentFilter filter);
}
