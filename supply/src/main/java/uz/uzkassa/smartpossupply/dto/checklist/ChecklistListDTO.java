package uz.uzkassa.smartpossupply.dto.checklist;

import lombok.Getter;
import lombok.Setter;
import uz.uzkassa.smartpossupply.dto.base.CommonDTO;
import uz.uzkassa.smartpossupply.enums.ChecklistStatus;

import java.io.Serializable;
import java.time.LocalDateTime;

@Getter
@Setter
public class ChecklistListDTO extends CommonDTO implements Serializable {

    LocalDateTime checklistDate;

    CommonDTO agent;

    CommonDTO supervisor;

    ChecklistStatus status;
}
