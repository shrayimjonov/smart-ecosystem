package uz.uzkassa.cts.dto.vacancyapplication.admin;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import uz.uzkassa.cts.dto.base.CommonDTO;
import uz.uzkassa.cts.enums.VacancyApplicationStatus;

import java.io.Serializable;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
@NoArgsConstructor
public class VacancyApplicationDetailAdminDTO extends CommonDTO implements Serializable {

    String vacancyApplicationNumber;

    CommonDTO company;

    CommonDTO branch;

    CommonDTO department;

    CommonDTO position;

    CommonDTO grade;

    CommonDTO group;

    CommonDTO template;

    CommonDTO approver;

    String responsibility;

    String requirement;

    String condition;

    Integer qty;

    VacancyApplicationStatus status;

    String reason;
}
