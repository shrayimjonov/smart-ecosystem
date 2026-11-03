package uz.uzkassa.cts.service;

import org.springframework.data.domain.Page;
import uz.uzkassa.cts.domain.hrms.LeaveRequest;
import uz.uzkassa.cts.dto.leaverequest.LeaveRequestDTO;
import uz.uzkassa.cts.dto.leaverequest.LeaveRequestDetailDTO;
import uz.uzkassa.cts.dto.leaverequest.LeaveRequestListDTO;
import uz.uzkassa.cts.filter.BaseFilter;

public interface LeaveRequestService extends BaseService<LeaveRequest, Long> {

    Long create(LeaveRequestDTO leaveRequestDTO);

    Long update(Long id, LeaveRequestDTO leaveRequestDTO);

    LeaveRequestDetailDTO get(Long id);

    Page<LeaveRequestListDTO> getList(BaseFilter filter);

}
