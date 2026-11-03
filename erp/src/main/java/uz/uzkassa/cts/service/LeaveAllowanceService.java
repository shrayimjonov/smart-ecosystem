package uz.uzkassa.cts.service;

import org.springframework.data.domain.Page;
import uz.uzkassa.cts.domain.hrms.LeaveAllowance;
import uz.uzkassa.cts.dto.leaveallowance.LeaveAllowanceDTO;
import uz.uzkassa.cts.dto.leaveallowance.LeaveAllowanceDetailDTO;
import uz.uzkassa.cts.dto.leaveallowance.LeaveAllowanceListDTO;
import uz.uzkassa.cts.filter.BaseFilter;
import uz.uzkassa.cts.filter.LeaveAllowanceFilter;

public interface LeaveAllowanceService extends BaseService<LeaveAllowance, Long> {

    Long create(LeaveAllowanceDTO leaveAllowanceDTO);

    void create(Long employeeId);

    Long update(Long id, LeaveAllowanceDTO leaveAllowanceDTO);

    LeaveAllowanceDetailDTO get(Long id);

    Page<LeaveAllowanceListDTO> getList(LeaveAllowanceFilter filter);

    void recalculate(Long employeeId, Integer allowance, Integer year);
}
