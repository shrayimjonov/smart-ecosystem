package uz.uzkassa.cts.dto.employmentInformation;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import uz.uzkassa.cts.enums.EmploymentType;

import java.time.LocalDate;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class EmploymentInformationDTO {

    Long id;

    Long companyId;

    Long branchId;

    Long departmentId;

    Long positionId;

    Long gradeId;

    Long supervisorId;

    LocalDate hireDate;

    Long timeslotId;

    EmploymentType type;
}