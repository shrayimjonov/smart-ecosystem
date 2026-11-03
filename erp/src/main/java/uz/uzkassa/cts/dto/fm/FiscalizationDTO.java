package uz.uzkassa.cts.dto.fm;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import uz.uzkassa.cts.enums.DeviceStatus;
import uz.uzkassa.cts.enums.OfdStatus;

import java.io.Serializable;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
@NoArgsConstructor
public class FiscalizationDTO implements Serializable {

    Long branchId;

    @NotNull
    Long customerId;

    @NotNull
    Long customerBranchId;

    @NotNull
    @NotEmpty
    String deviceSerial;

    @NotNull
    Long deviceModelId;

    @NotNull
    @NotEmpty
    String fiscalModuleSerial;

    DeviceStatus status;

    OfdStatus ofdStatus;
}
