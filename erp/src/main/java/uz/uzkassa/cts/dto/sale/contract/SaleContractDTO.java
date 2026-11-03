package uz.uzkassa.cts.dto.sale.contract;

import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import uz.uzkassa.cts.dto.sale.accessory.SaleAccessoryDTO;

import java.io.Serializable;
import java.util.List;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
@NoArgsConstructor
public class SaleContractDTO implements Serializable {

    @NotNull
    private Long customerId;

    @NotNull
    List<SaleAccessoryDTO> accessoryList;
}
