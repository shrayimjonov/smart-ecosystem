package uz.uzkassa.smartpossupply.dto.checklist;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import uz.uzkassa.smartpossupply.enums.EntityTypeEnum;

import java.io.Serializable;

@Getter
@Setter
public class ChecklistItemDTO implements Serializable {

    Long id;

    @NotNull
    Long entityId;

    @NotNull
    EntityTypeEnum entityType;
}
