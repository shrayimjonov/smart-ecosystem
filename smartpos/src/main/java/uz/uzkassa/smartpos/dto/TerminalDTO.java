package uz.uzkassa.smartpos.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import uz.uzkassa.smartpos.enums.DeviceType;

@Getter
@Setter
@ToString
@NoArgsConstructor
public class TerminalDTO extends BaseAuditDTO {
    @NotBlank
    private String serialNumber;
    private Long branchId;
    private String branchName;
    private String branchAddress;
    private Long companyId;
    private String companyName;
    private String companyInn;
    private String companyAddress;
    private String statusName;
    private String fiscalModuleSN;
    private Long fiscalModuleId;
    private DeviceType deviceType;

    public TerminalDTO(String serialNumber, Long branchId, String branchName) {
        this.serialNumber = serialNumber;
        this.branchId = branchId;
        this.branchName = branchName;
    }
}
