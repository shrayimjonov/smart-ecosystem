package uz.uzkassa.cts.service;

import org.springframework.data.domain.Page;
import uz.uzkassa.cts.domain.hrms.Event;
import uz.uzkassa.cts.dto.event.EventDTO;
import uz.uzkassa.cts.dto.event.admin.EventDetailAdminDTO;
import uz.uzkassa.cts.dto.event.admin.EventListAdminDTO;
import uz.uzkassa.cts.enums.EventStatus;
import uz.uzkassa.cts.filter.EventFilter;

public interface EventService extends BaseService<Event, Long> {
    Long create(EventDTO eventDTO);

    Long update(Long id, EventDTO eventDTO);

    EventDetailAdminDTO getForAdmin(Long id);

    Page<EventListAdminDTO> getListForAdmin(EventFilter filter);

    void updateStatus(Long id, EventStatus status);
}
