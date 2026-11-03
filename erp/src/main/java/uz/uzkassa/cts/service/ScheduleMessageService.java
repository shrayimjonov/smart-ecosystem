package uz.uzkassa.cts.service;

import org.springframework.data.domain.Page;
import uz.uzkassa.cts.dto.schedulemessage.ScheduleMessageDTO;
import uz.uzkassa.cts.dto.schedulemessage.ScheduleMessageDetailDTO;
import uz.uzkassa.cts.dto.schedulemessage.ScheduleMessageListDTO;
import uz.uzkassa.cts.enums.ScheduleMessageStatus;
import uz.uzkassa.cts.filter.BaseFilter;

public interface ScheduleMessageService {

    Long create(ScheduleMessageDTO scheduleMessageDTO);

    Long update(Long id, ScheduleMessageDTO scheduleMessageDTO);

    ScheduleMessageDetailDTO get(Long id);

    void updateStatus(Long id, ScheduleMessageStatus status);

    Page<ScheduleMessageListDTO> getList(BaseFilter filter);

    void deleteById(Long id);
}
