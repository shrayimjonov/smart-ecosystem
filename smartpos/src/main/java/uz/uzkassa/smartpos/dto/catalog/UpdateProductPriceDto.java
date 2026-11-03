package uz.uzkassa.smartpos.dto.catalog;

import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.io.Serializable;
import java.math.BigDecimal;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class UpdateProductPriceDto implements Serializable {
    @NotNull
    private Long productId;
    @NotNull
    private Long branchId;
    @NotNull
    private BigDecimal salesPrice;
}
