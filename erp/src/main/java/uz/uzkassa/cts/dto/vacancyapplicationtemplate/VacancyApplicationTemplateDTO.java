package uz.uzkassa.cts.dto.vacancyapplicationtemplate;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import uz.uzkassa.cts.dto.base.CommonDTO;

import java.io.Serializable;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
@NoArgsConstructor
public class VacancyApplicationTemplateDTO extends CommonDTO implements Serializable {

    Long companyId;

    Long branchId;

    Long departmentId;

    Long positionId;

    String responsibility;

    String requirement;

    String condition;

    Long groupId;

    Long gradeId;
}
