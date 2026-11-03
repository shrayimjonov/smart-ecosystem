package uz.uzkassa.cts.service;

import uz.uzkassa.cts.domain.hrms.EmploymentInformation;
import uz.uzkassa.cts.dto.employmentInformation.EmploymentInformationDTO;
import uz.uzkassa.cts.dto.employmentInformation.EmploymentInformationDetailDTO;


public interface EmploymentInformationService extends BaseService<EmploymentInformation, Long> {

    Long create(Long employeeId, EmploymentInformationDTO employmentInformationDTO);

    Long update(Long employeeId, EmploymentInformationDTO employmentInformationDTO);

    EmploymentInformationDetailDTO get(Long employeeId);

}
