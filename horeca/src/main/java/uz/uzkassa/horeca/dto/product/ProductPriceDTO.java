package uz.uzkassa.horeca.dto.product;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import uz.uzkassa.horeca.dto.base.CommonDTO;

import java.io.Serial;
import java.io.Serializable;
import java.math.BigDecimal;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ProductPriceDTO extends CommonDTO implements Serializable {

    @Serial
    private static final long serialVersionUID = 3534543L;

    BigDecimal price;

    public ProductPriceDTO(Long id, String name, BigDecimal price) {
        super(id, name);
        this.price = price;
    }
}
