package uz.uzkassa.cts.dto.sale.contract;

import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import uz.uzkassa.cts.enums.ContractStatus;

import java.io.Serializable;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
@NoArgsConstructor
public class SaleContractUpdateStatusDTO implements Serializable {

    @NotNull
    Long id;

    @NotNull
    ContractStatus status;
}
