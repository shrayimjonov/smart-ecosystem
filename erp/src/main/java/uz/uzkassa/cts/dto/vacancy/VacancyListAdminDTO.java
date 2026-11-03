package uz.uzkassa.cts.dto.vacancy;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import uz.uzkassa.cts.dto.base.CommonDTO;
import uz.uzkassa.cts.enums.VacancyStatus;

import java.io.Serializable;
import java.time.LocalDateTime;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
@NoArgsConstructor
public class VacancyListAdminDTO extends CommonDTO implements Serializable {

    String vacancyNumber;

    CommonDTO company;

    CommonDTO branch;

    CommonDTO department;

    CommonDTO position;

    CommonDTO grade;

    CommonDTO application;

    CommonDTO creator;

    CommonDTO hrManager;

    LocalDateTime createdDate;

    VacancyStatus status;
}
