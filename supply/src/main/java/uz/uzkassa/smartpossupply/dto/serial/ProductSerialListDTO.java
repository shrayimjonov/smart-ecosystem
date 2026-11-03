package uz.uzkassa.smartpossupply.dto.serial;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import uz.uzkassa.smartpossupply.dto.product.ProductCommonDTO;
import uz.uzkassa.smartpossupply.enums.ProductSerialStatus;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Powered by: Shuxratjon Rayimjonov
 * Date: 11.05.2023
 */
@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ProductSerialListDTO implements Serializable {

    Long id;

    String serialNumber;

    ProductCommonDTO product;

    LocalDateTime createdDate;

    ProductSerialStatus status;

    BigDecimal qty;

    BigDecimal basePrice;
}
