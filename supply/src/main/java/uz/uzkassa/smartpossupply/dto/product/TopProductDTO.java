package uz.uzkassa.smartpossupply.dto.product;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

import java.math.BigDecimal;

@Setter
@Getter
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class TopProductDTO {

    Long productId;

    String productName;

    BigDecimal percentage;

    BigDecimal qty;

    String unitName;

    public TopProductDTO(Long productId, String productName, BigDecimal percentage, BigDecimal qty, String unitName) {
        this.productId = productId;
        this.productName = productName;
        this.percentage = percentage;
        this.qty = qty;
        this.unitName = unitName;
    }
}
