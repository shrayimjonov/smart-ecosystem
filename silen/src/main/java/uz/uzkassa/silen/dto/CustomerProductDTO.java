package uz.uzkassa.silen.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * A DTO for the {@link uz.uzkassa.silen.domain.CustomerProduct} entity.
 */
@Getter
@Setter
public class CustomerProductDTO extends BaseIdentityDTO implements Serializable {
    @NotNull
    private Long customerId;
    private String productId;
    private String productName;
    private BigDecimal discountPercent;
    private BigDecimal discount;
    private BigDecimal price;
    private Boolean isSale;
}
