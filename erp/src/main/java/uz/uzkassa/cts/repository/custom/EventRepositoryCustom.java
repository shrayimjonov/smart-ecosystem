package uz.uzkassa.cts.repository.custom;

import uz.uzkassa.cts.domain.hrms.Event;
import uz.uzkassa.cts.dto.base.ResultList;
import uz.uzkassa.cts.filter.EventFilter;

public interface EventRepositoryCustom {

    ResultList<Event> getResultList(EventFilter filter);
}
