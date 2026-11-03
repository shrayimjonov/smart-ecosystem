package uz.uzkassa.cts.dto.employmentInformation;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import uz.uzkassa.cts.dto.base.CommonDTO;
import uz.uzkassa.cts.enums.EmploymentType;
import uz.uzkassa.cts.enums.UserStatus;

import java.time.LocalDate;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class EmploymentInformationDetailDTO {

    Long id;

    CommonDTO employee;

    String employeeNumber;

    CommonDTO company;

    CommonDTO branch;

    CommonDTO department;

    CommonDTO position;

    CommonDTO grade;

    CommonDTO supervisor;

    LocalDate hireDate;

    LocalDate resignationDate;

    CommonDTO timeslot;

    UserStatus status;

    EmploymentType type;

}

