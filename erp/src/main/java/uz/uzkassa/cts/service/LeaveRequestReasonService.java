package uz.uzkassa.cts.service;

import org.springframework.data.domain.Page;
import uz.uzkassa.cts.domain.hrms.LeaveRequestReason;
import uz.uzkassa.cts.dto.leaverequestreason.LeaveRequestReasonDTO;
import uz.uzkassa.cts.dto.leaverequestreason.LeaveRequestReasonDetailDTO;
import uz.uzkassa.cts.dto.leaverequestreason.LeaveRequestReasonItemDTO;
import uz.uzkassa.cts.dto.leaverequestreason.LeaveRequestReasonListDTO;
import uz.uzkassa.cts.filter.LeaveRequestReasonFilter;

import java.util.List;


public interface LeaveRequestReasonService extends BaseService<LeaveRequestReason, Long> {

    Long create(LeaveRequestReasonDTO LeaveRequestReasonDTO);

    Long update(Long id, LeaveRequestReasonDTO LeaveRequestReasonDTO);

    LeaveRequestReasonDetailDTO get(Long id);

    Page<LeaveRequestReasonListDTO> getList(LeaveRequestReasonFilter filter);

    List<LeaveRequestReasonItemDTO> getItems();
}
