package uz.uzkassa.smartpossupply.dto.product;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import uz.uzkassa.smartpossupply.dto.base.CommonDTO;

import java.math.BigDecimal;

@Setter
@Getter
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ProductResidueDTO  extends CommonDTO {

    BigDecimal qty;

    BigDecimal percentage;

    String unitName;

    public ProductResidueDTO(Long id, String name, BigDecimal qty, BigDecimal percentage, String unitName) {
        this.id = id;
        this.name = name;
        this.qty = qty;
        this.percentage = percentage;
        this.unitName = unitName;
    }
}
