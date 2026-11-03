package uz.uzkassa.cts.dto.vacancyapplication.admin;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import uz.uzkassa.cts.dto.base.CommonDTO;
import uz.uzkassa.cts.dto.file.FileListDTO;
import uz.uzkassa.cts.enums.VacancyApplicationStatus;

import java.io.Serializable;
import java.time.LocalDateTime;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
@NoArgsConstructor
public class VacancyApplicationListAdminDTO extends CommonDTO implements Serializable {

    String vacancyApplicationNumber;

    CommonDTO company;

    CommonDTO branch;

    CommonDTO department;

    CommonDTO position;

    CommonDTO grade;

    CommonDTO group;

    CommonDTO approver;

    CommonDTO creator;

    VacancyApplicationStatus status;

    LocalDateTime createdDate;

    FileListDTO file;

    String requirement;

    Integer qty;
}
