package uz.uzkassa.cts.service.impl;

import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uz.uzkassa.cts.domain.User;
import uz.uzkassa.cts.domain.hrms.EmploymentInformation;
import uz.uzkassa.cts.dto.employmentInformation.EmploymentInformationDTO;
import uz.uzkassa.cts.dto.employmentInformation.EmploymentInformationDetailDTO;
import uz.uzkassa.cts.enums.Prefix;
import uz.uzkassa.cts.numbering.NumberingContext;
import uz.uzkassa.cts.repository.EmploymentInformationRepository;
import uz.uzkassa.cts.service.EmploymentInformationService;
import uz.uzkassa.cts.validator.ValidationContext;

import java.util.Map;

import static uz.uzkassa.cts.constants.Constants.INCREMENT_NUMBER;
import static uz.uzkassa.cts.constants.Constants.NUMBER;

@Transactional(readOnly = true)
@Service
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class EmploymentInformationServiceImpl extends BaseServiceImpl<EmploymentInformation, Long> implements EmploymentInformationService {

    EmploymentInformationRepository employmentInformationRepository;
    ValidationContext validationContext;
    NumberingContext numberingContext;

    public EmploymentInformationServiceImpl(EmploymentInformationRepository employmentInformationRepository, ValidationContext validationContext, NumberingContext numberingContext) {
        super(employmentInformationRepository);
        this.employmentInformationRepository = employmentInformationRepository;
        this.validationContext = validationContext;
        this.numberingContext = numberingContext;
    }

    @Override
    @Transactional
    public Long create(Long employeeId, EmploymentInformationDTO employmentInformationDTO) {
        validationContext.validate(employmentInformationDTO);
        User employee = userRepository.findById(employeeId).orElseThrow(validationService.entityNotFoundExceptionThrow(EMPLOYEE));
        Map<String, String> numberingMap = numberingContext.generateNumber(Prefix.EMPLOYEE, employmentInformationDTO.getCompanyId());
        EmploymentInformation employmentInformation = employmentInformationRepository.findFirstByEmployeeIdAndDeletedIsFalse(employeeId).orElseGet(EmploymentInformation::new);
        employmentInformation.setEmployeeId(employeeId);
        employmentInformation.setEmployeeNumber(numberingMap.get(NUMBER));
        employmentInformation.setIncrementNumber(Long.valueOf(numberingMap.get(INCREMENT_NUMBER)));
        employmentInformation.setGradeId(employmentInformationDTO.getGradeId());
        employmentInformation.setSupervisorId(employmentInformationDTO.getSupervisorId());
        employmentInformation.setHireDate(employmentInformationDTO.getHireDate());
        employmentInformation.setType(employmentInformationDTO.getType());
        employmentInformationRepository.save(employmentInformation);

        employee.setCompanyId(employmentInformationDTO.getCompanyId());
        employee.setBranchId(employmentInformationDTO.getBranchId());
        employee.setDepartmentId(employmentInformationDTO.getDepartmentId());
        employee.setPositionId(employmentInformationDTO.getPositionId());
        employee.setTimeslotId(employmentInformationDTO.getTimeslotId());
        userRepository.save(employee);
        return employmentInformation.getId();
    }

    @Override
    @Transactional
    public Long update(Long employeeId, EmploymentInformationDTO employmentInformationDTO) {
        validationContext.validate(employmentInformationDTO);
        return employmentInformationRepository.findFirstByEmployeeIdAndDeletedIsFalse(employeeId).map(employmentInformation -> {
            employmentInformation.setGradeId(employmentInformationDTO.getGradeId());
            employmentInformation.setSupervisorId(employmentInformationDTO.getSupervisorId());
            employmentInformation.setHireDate(employmentInformationDTO.getHireDate());
            employmentInformation.setType(employmentInformationDTO.getType());
            employmentInformationRepository.save(employmentInformation);

            User employee = employmentInformation.getEmployee();
            employee.setCompanyId(employmentInformationDTO.getCompanyId());
            employee.setBranchId(employmentInformationDTO.getBranchId());
            employee.setDepartmentId(employmentInformationDTO.getDepartmentId());
            employee.setPositionId(employmentInformationDTO.getPositionId());
            employee.setTimeslotId(employmentInformationDTO.getTimeslotId());
            userRepository.save(employee);

            return employmentInformation.getId();
        }).orElseThrow(validationService.entityNotFoundExceptionThrow(EMPLOYMENT_INFORMATION));
    }

    @Override
    public EmploymentInformationDetailDTO get(Long employeeId) {
        return employmentInformationRepository.findFirstByEmployeeIdAndDeletedIsFalse(employeeId).map(employmentInformation -> {
            EmploymentInformationDetailDTO employmentInformationDetailDTO = new EmploymentInformationDetailDTO();
            employmentInformationDetailDTO.setId(employmentInformation.getId());
            employmentInformationDetailDTO.setEmployeeNumber(employmentInformation.getEmployeeNumber());
            if (employmentInformation.getEmployee() != null) {
                User employee = employmentInformation.getEmployee();
                employmentInformationDetailDTO.setEmployee(employee.toCommonDTO());
                employmentInformationDetailDTO.setStatus(employee.getStatus());
                if (employee.getCompany() != null) {
                    employmentInformationDetailDTO.setCompany(employee.getCompany().toCommonDTO());
                }
                if (employee.getBranch() != null) {
                    employmentInformationDetailDTO.setBranch(employee.getBranch().toCommonDTO());
                }
                if (employee.getDepartment() != null) {
                    employmentInformationDetailDTO.setDepartment(employee.getDepartment().toCommonDTO());
                }
                if (employee.getPosition() != null) {
                    employmentInformationDetailDTO.setPosition(employee.getPosition().toCommonDTO());
                }
                if (employee.getTimeslot() != null) {
                    employmentInformationDetailDTO.setTimeslot(employee.getTimeslot().toCommonDTO());
                }
            }
            if (employmentInformation.getGrade() != null) {
                employmentInformationDetailDTO.setGrade(employmentInformation.getGrade().toCommonDTO());
            }
            if (employmentInformation.getSupervisor() != null) {
                employmentInformationDetailDTO.setSupervisor(employmentInformation.getSupervisor().toCommonDTO());
            }
            employmentInformationDetailDTO.setHireDate(employmentInformation.getHireDate());
            employmentInformationDetailDTO.setResignationDate(employmentInformation.getResignationDate());
            employmentInformationDetailDTO.setType(employmentInformation.getType());

            return employmentInformationDetailDTO;
        }).orElse(null);
    }
}
