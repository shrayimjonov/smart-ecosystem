package uz.uzkassa.cts.service;

import org.springframework.data.domain.Page;
import uz.uzkassa.cts.domain.hrms.EventRequest;
import uz.uzkassa.cts.dto.base.CommonDTO;
import uz.uzkassa.cts.dto.eventrequest.EventRequestDTO;
import uz.uzkassa.cts.dto.eventrequest.admin.EventRequestDetailAdminDTO;
import uz.uzkassa.cts.dto.eventrequest.admin.EventRequestListAdminDTO;
import uz.uzkassa.cts.enums.ApproverCommonStatus;
import uz.uzkassa.cts.filter.EventRequestFilter;

import java.util.List;

public interface EventRequestService extends BaseService<EventRequest, Long> {
    Long create(EventRequestDTO eventRequestDTO);

    Long update(Long id, EventRequestDTO eventRequestDTO);

    EventRequestDetailAdminDTO getForAdmin(Long id);

    Page<EventRequestListAdminDTO> getListForAdmin(EventRequestFilter filter);

    List<CommonDTO> lookUp(EventRequestFilter filter);

    void updateStatus(Long id, ApproverCommonStatus status);
}
