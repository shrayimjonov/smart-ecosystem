package uz.uzkassa.cts.dto.sale.accessory;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@Getter
@Setter
public class SaleAccessoryDTO implements Serializable {
    @NotNull
    private Long accessoryId;
    @NotNull
    private Integer qty;
}
