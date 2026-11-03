package uz.uzkassa.cts.dto.vacancyapplication;

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
public class VacancyApplicationDTO extends CommonDTO implements Serializable {

    Long companyId;

    Long branchId;

    Long departmentId;

    Long positionId;

    Long gradeId;

    Long groupId;

    String responsibility;

    String requirement;

    String condition;

    Integer qty;

    Long approverId;

    Long templateId;
}
