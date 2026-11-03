package uz.uzkassa.silen.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import uz.uzkassa.silen.enumeration.ServiceType;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
public class GivePermissionDTO extends BaseAuditDTO {
    @NotNull
    String parentId;

    @NotNull
    String childId;

    @NotNull
    @Min(value = 1)
    Integer quantity;

    @NotNull
    ServiceType from;

    String fromName;

    @NotNull
    ServiceType to;

    String toName;

    LocalDateTime createdDate;
}
