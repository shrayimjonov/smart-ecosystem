package uz.uzkassa.cts.dto.casestatuslog;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import uz.uzkassa.cts.dto.base.CommonDTO;
import uz.uzkassa.cts.enums.CaseStatus;

import java.time.LocalDateTime;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CaseStatusLogListDTO {

    CommonDTO cases;

    CaseStatus status;

    LocalDateTime createdDate;

}
