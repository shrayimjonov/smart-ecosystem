package uz.uzkassa.smartpossupply.dto.checklist;

import lombok.Getter;
import lombok.Setter;
import uz.uzkassa.smartpossupply.dto.base.CommonDTO;
import uz.uzkassa.smartpossupply.enums.ChecklistStatus;
import uz.uzkassa.smartpossupply.enums.EntityTypeEnum;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
public class ChecklistDetailDTO extends CommonDTO implements Serializable {

    Long entityId;

    EntityTypeEnum entityType;

    LocalDateTime checklistDate;

    CommonDTO entity;

    CommonDTO agent;

    CommonDTO supervisor;

    ChecklistStatus status;

    List<ChecklistItemDetailDTO> items;

    List<CommonDTO> conditions;

    List<ChecklistQuestionDTO> questions;
}
