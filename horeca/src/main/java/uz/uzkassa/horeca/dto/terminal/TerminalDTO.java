package uz.uzkassa.horeca.dto.terminal;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import uz.uzkassa.horeca.enums.terminal.TerminalModel;
import uz.uzkassa.horeca.enums.terminal.TerminalStatus;

import java.io.Serial;
import java.io.Serializable;

@Getter
@Setter
@ToString
@NoArgsConstructor
public class TerminalDTO implements Serializable {

    @Serial
    private static final long serialVersionUID = 45654645L;

    @NotBlank
    private String serialNumber;

    private TerminalModel model;

    private Long branchId;

    private String branchName;

    private String branchAddress;

    private Long companyId;
    private String companyName;
    private String companyInn;
    private String companyAddress;
    private TerminalStatus status;
    private String statusName;
    private String fiscalModuleSN;
    private Long fiscalModuleId;

    public TerminalDTO(String serialNumber, Long branchId, String branchName) {
        this.serialNumber = serialNumber;
        this.branchId = branchId;
        this.branchName = branchName;
    }
}
