package uz.uzkassa.cts.dto.vacancy;

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
public class VacancyDetailAdminDTO extends CommonDTO implements Serializable {

    String vacancyNumber;

    CommonDTO company;

    CommonDTO branch;

    CommonDTO department;

    CommonDTO position;

    CommonDTO grade;

    CommonDTO hrManager;

    String responsibility;

    String requirement;

    String condition;

    CommonDTO application;
}
