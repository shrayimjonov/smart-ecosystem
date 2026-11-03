package uz.uzkassa.cts.service;

import org.springframework.data.domain.Page;
import uz.uzkassa.cts.domain.hrms.SalaryAllocation;
import uz.uzkassa.cts.dto.salaryAllocation.SalaryAllocationDTO;
import uz.uzkassa.cts.dto.salaryAllocation.SalaryAllocationDetailDTO;
import uz.uzkassa.cts.dto.salaryAllocation.SalaryAllocationListDTO;
import uz.uzkassa.cts.filter.SalaryAllocationFilter;

import java.util.List;


public interface SalaryAllocationService extends BaseService<SalaryAllocation, Long>{

    Long create(SalaryAllocationDTO salaryAllocationListDTO);

    Long update(Long id, SalaryAllocationDTO salaryAllocationDTO);

    SalaryAllocationDetailDTO get(Long id);

    Page<SalaryAllocationListDTO> getList(SalaryAllocationFilter salaryAllocationFilter);




}
