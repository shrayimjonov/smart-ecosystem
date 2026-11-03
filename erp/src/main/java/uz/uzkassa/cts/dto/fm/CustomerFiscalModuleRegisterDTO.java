package uz.uzkassa.cts.dto.fm;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

import java.io.Serializable;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
@NoArgsConstructor
public class CustomerFiscalModuleRegisterDTO implements Serializable {

    @NotNull
    Long customerId;

    @NotNull
    Long customerBranchId;

    @NotNull
    @NotEmpty
    String serialNumber;

    boolean fromRegistry = true;

}
