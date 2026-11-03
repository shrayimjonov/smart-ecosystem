package uz.uzkassa.smartpossupply.repository;

import org.springframework.stereotype.Repository;
import uz.uzkassa.smartpossupply.domain.mailing.ScheduleMessage;
import uz.uzkassa.smartpossupply.dto.base.ResultList;
import uz.uzkassa.smartpossupply.filters.BaseFilter;

import java.util.List;

@Repository
public interface ScheduleMessageRepositoryCustom {
    ResultList<ScheduleMessage> getResultList(BaseFilter filter);

    List<ScheduleMessage> lookUp(BaseFilter filter);
}
