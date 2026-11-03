package uz.uzkassa.cts.service;

import org.springframework.data.domain.Page;
import uz.uzkassa.cts.domain.hrms.Department;
import uz.uzkassa.cts.dto.base.CommonDTO;
import uz.uzkassa.cts.dto.base.CommonTreeDTO;
import uz.uzkassa.cts.dto.department.DepartmentDTO;
import uz.uzkassa.cts.dto.department.admin.DepartmentDetailAdminDTO;
import uz.uzkassa.cts.dto.department.admin.DepartmentListAdminDTO;
import uz.uzkassa.cts.enums.CommonStatus;
import uz.uzkassa.cts.filter.BaseFilter;

import java.util.List;

public interface DepartmentService extends BaseService<Department, Long> {

    Long create(DepartmentDTO departmentDTO);

    Long update(Long id, DepartmentDTO departmentDTO);

    Page<DepartmentListAdminDTO> getListForAdmin(BaseFilter filter);

    DepartmentDetailAdminDTO getForAdmin(Long id);

    List<CommonDTO> lookUp(BaseFilter filter);

    void updateStatus(Long id, CommonStatus status);

    List<CommonTreeDTO> treeList(BaseFilter filter);
}
