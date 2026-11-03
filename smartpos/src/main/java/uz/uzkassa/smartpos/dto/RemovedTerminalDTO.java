package uz.uzkassa.smartpos.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RemovedTerminalDTO {
    @NotBlank
    private String serialNumber;
}
