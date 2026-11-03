package uz.uzkassa.smartpossupply.dto.receipt;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import uz.uzkassa.smartpossupply.enums.Vat;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * Powered by: Shuxratjon Rayimjonov
 * Date: 20.12.2022 16:02
 */
@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ReceiptItemBaseDTO implements Serializable {

    BigDecimal excise = BigDecimal.ZERO;

    BigDecimal price;

    BigDecimal qty;

    BigDecimal amount;

    BigDecimal discountPercent = BigDecimal.ZERO;

    BigDecimal discount = BigDecimal.ZERO;

    Vat vat;
}
