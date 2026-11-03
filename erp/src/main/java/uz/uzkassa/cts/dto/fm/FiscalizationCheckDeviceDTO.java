package uz.uzkassa.cts.dto.fm;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import uz.uzkassa.cts.enums.terminal.TerminalOfdStatus;
import uz.uzkassa.cts.enums.terminal.TerminalStatus;

import java.io.Serializable;
import java.time.LocalDateTime;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
@NoArgsConstructor
public class FiscalizationCheckDeviceDTO implements Serializable {

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

    LocalDateTime fiscalizationDate;

    @NotNull
    @NotEmpty
    String fiscalModuleSerial;

    TerminalStatus status;

    TerminalOfdStatus ofdStatus;

    boolean sendGeolocation;
}
