package uz.uzkassa.cts.dto.vacancyapplicationtemplate.admin;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import uz.uzkassa.cts.dto.base.CommonDTO;
import uz.uzkassa.cts.dto.company.CompanyCommonDTO;

import java.io.Serializable;
import java.time.LocalDateTime;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
@NoArgsConstructor
public class VacancyApplicationTemplateListAdminDTO extends CommonDTO implements Serializable {

    CompanyCommonDTO company;

    CommonDTO branch;

    CommonDTO department;

    CommonDTO group;

    CommonDTO position;

    CommonDTO grade;

    CommonDTO creator;

    LocalDateTime createdDate;
}
