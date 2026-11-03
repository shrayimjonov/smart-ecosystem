package uz.uzkassa.smartpossupply.dto.serial;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.math.BigDecimal;

/**
 * Powered by: Shuxratjon Rayimjonov
 * Date: 11.05.2023
 */
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ProductSerialDTO {

    Long id;

    String serialNumber;

    BigDecimal qty;

    BigDecimal baseSumma;

    Long productId;
}
