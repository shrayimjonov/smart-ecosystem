package uz.uzkassa.horeca.dto.terminal;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TerminalUpdateDTO {

    Long id;

    @NotBlank
    private String serialNumber;

    @NotBlank
    private Long branchId;

    @NotBlank
    private String fiscalModuleSN;

}
