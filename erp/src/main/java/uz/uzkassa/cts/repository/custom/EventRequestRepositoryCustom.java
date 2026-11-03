package uz.uzkassa.cts.repository.custom;

import uz.uzkassa.cts.domain.hrms.EventRequest;
import uz.uzkassa.cts.dto.base.ResultList;
import uz.uzkassa.cts.filter.EventRequestFilter;

public interface EventRequestRepositoryCustom {

    ResultList<EventRequest> getResultList(EventRequestFilter filter);
}
