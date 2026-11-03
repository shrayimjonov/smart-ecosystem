package uz.uzkassa.smartpos.integration.cto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import uz.uzkassa.smartpos.enums.DeviceType;

import java.io.Serializable;

@Getter
@Setter
@NoArgsConstructor
public class CtoResultDTO implements Serializable {
    @NotNull
    private CtoCompanyDTO customer;
    @NotNull
    private CtoBranchDTO customerBranch;
    @NotBlank
    private String deviceSerial;
    private DeviceType deviceType;

    public CtoResultDTO(@NotNull String deviceSerial) {
        this.deviceSerial = deviceSerial;
    }
}
