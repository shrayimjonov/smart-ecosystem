package uz.uzkassa.smartpossupply.dto.checklist;

import lombok.Getter;
import lombok.Setter;
import uz.uzkassa.smartpossupply.dto.base.CommonDTO;
import uz.uzkassa.smartpossupply.enums.EntityTypeEnum;

import java.io.Serializable;
import java.util.List;

@Getter
@Setter
public class ChecklistItemDetailDTO implements Serializable {

    Long id;

    Long entityId;

    EntityTypeEnum entityType;

    CommonDTO entity;

    List<ChecklistResultDTO> result;
}
