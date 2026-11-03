package uz.uzkassa.cts.dto.ofd;

import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

import java.io.Serializable;

@Getter
@Setter
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CustomerFiscalDTO implements Serializable {

    @NotNull
    Long customerId;

    @NotNull
    Long customerBranchId;

    @NotNull
    String deviceSerial;

    @NotNull
    String fiscalModuleSerial;
}
