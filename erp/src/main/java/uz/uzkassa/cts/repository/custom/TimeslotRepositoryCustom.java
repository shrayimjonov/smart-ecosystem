package uz.uzkassa.cts.repository.custom;

import org.springframework.stereotype.Repository;
import uz.uzkassa.cts.domain.timeslot.Timeslot;
import uz.uzkassa.cts.dto.base.ResultList;
import uz.uzkassa.cts.filter.TimeslotFilter;

@Repository
public interface TimeslotRepositoryCustom {

    ResultList<Timeslot> getResultList(TimeslotFilter filter);
}
