package uz.uzkassa.horeca.dto.receipt.cabinet;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.FieldDefaults;
import uz.uzkassa.horeca.dto.base.CommonDTO;
import uz.uzkassa.horeca.dto.product.ProductCommonDTO;
import uz.uzkassa.horeca.enums.Vat;

import java.io.Serial;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Set;


@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
@ToString
public class ReceiptItemCabinetDTO implements Serializable {

    @Serial
    private static final long serialVersionUID = 5645654634L;

    Long id;

    ProductCommonDTO product;

    CommonDTO unit;

    CommonDTO category;

    BigDecimal price;

    BigDecimal salesPrice;

    BigDecimal qty;

    BigDecimal discountPercent;

    BigDecimal discount;

    BigDecimal vatPrice;

    BigDecimal vatRate;

    Set<String> marks;

    Integer origin;
}
