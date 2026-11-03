package uz.uzkassa.cts.service;

import uz.uzkassa.cts.domain.Company;
import uz.uzkassa.cts.dto.companystructure.company.CompanyStructureCompanyDTO;
import uz.uzkassa.cts.dto.companystructure.department.CompanyStructureDepartmentDTO;
import uz.uzkassa.cts.dto.companystructure.position.CompanyStructurePositionDTO;
import uz.uzkassa.cts.dto.companystructure.supervisor.CompanyStructureSupervisorDTO;

public interface CompanyStructureService extends BaseService<Company, Long> {

    CompanyStructureCompanyDTO getByCompany(Long companyId);

    CompanyStructurePositionDTO getByPosition(Long companyId);

    CompanyStructureDepartmentDTO getByDepartment(Long companyId);

    CompanyStructureSupervisorDTO getBySupervisor(Long companyId);
}
