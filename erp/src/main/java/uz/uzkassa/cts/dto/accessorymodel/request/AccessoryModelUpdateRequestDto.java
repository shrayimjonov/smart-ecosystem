package uz.uzkassa.cts.dto.accessorymodel.request;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AccessoryModelUpdateRequestDto extends AccessoryModelCreateRequestDto {
    @NotNull
    private Long id;
}
