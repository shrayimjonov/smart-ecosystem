package uz.uzkassa.smartpos.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;
import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
public class BasketItemUpdateDTO implements Serializable {
    @NotNull
    private Long supplierProductId;
    @NotNull
    private Long companyId;
    @NotNull
    private BigDecimal amount;
    private boolean selected = true;
}
