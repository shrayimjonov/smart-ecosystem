package uz.uzkassa.cts.dto.casestatuslog;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import uz.uzkassa.cts.enums.CaseStatus;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CaseStatusLogDTO {

    Long caseId;

    CaseStatus status;
}
