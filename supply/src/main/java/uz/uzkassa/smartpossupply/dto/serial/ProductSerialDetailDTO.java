package uz.uzkassa.smartpossupply.dto.serial;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import uz.uzkassa.smartpossupply.dto.product.ProductCommonDTO;
import uz.uzkassa.smartpossupply.enums.ProductSerialStatus;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Powered by: Shuxratjon Rayimjonov
 * Date: 11.055.2023
 */
@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ProductSerialDetailDTO {

    Long id;

    String serialNumber;

    LocalDateTime createdDate;

    BigDecimal qty;

    BigDecimal basePrice;

    ProductCommonDTO product;

    ProductSerialStatus status;
}
