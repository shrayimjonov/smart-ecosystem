package uz.uzkassa.smartpossupply.dto.serial;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import uz.uzkassa.smartpossupply.dto.product.ProductCommonDTO;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * Powered by: Shuxratjon Rayimjonov
 * Date: 11.05.2023
 */
@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
@NoArgsConstructor
public class ProductSerialLookUpDTO implements Serializable {

    Long id;

    String serialNumber;

    BigDecimal qty;

    BigDecimal baseSumma;

    ProductCommonDTO product;

    public ProductSerialLookUpDTO(Long id, String serialNumber, BigDecimal qty, BigDecimal baseSumma) {
        this.id = id;
        this.serialNumber = serialNumber;
        this.qty = qty;
        this.baseSumma = baseSumma;
    }
}
