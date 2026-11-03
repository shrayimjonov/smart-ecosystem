package uz.uzkassa.silen.dto;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

import java.math.BigDecimal;
import java.time.LocalDate;

@Getter
@Setter
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class SerialSelectItemDTO {
    String id;
    String serialNumber;
    String productId;
    String productName;
    LocalDate productionDate;
    String barcode;
    BigDecimal price;
    String packageName;
    private int qtyInAggregation;
    String description;
}
