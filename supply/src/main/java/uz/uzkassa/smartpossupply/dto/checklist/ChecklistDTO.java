package uz.uzkassa.smartpossupply.dto.checklist;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import uz.uzkassa.smartpossupply.dto.base.CommonDTO;
import uz.uzkassa.smartpossupply.enums.EntityTypeEnum;

import java.io.Serializable;
import java.util.List;

@Getter
@Setter
public class ChecklistDTO extends CommonDTO implements Serializable {

    @NotNull
    Long entityId;

    @NotNull
    EntityTypeEnum entityType;

    @NotNull
    @NotEmpty
    List<ChecklistItemDTO> items;

    @NotNull
    @NotEmpty
    List<Long> conditions;

    List<String> questions;

    Long supervisorId;
}
