package uz.uzkassa.cts.dto.product.admin;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import uz.uzkassa.cts.dto.base.CommonDTO;
import uz.uzkassa.cts.dto.vat.VatItemDTO;

import java.io.Serializable;
import java.math.BigDecimal;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ProductListAdminDTO extends CommonDTO implements Serializable {

    BigDecimal price;

    CommonDTO brand;

    CommonDTO category;

    VatItemDTO vat;

    CommonDTO unit;
}
