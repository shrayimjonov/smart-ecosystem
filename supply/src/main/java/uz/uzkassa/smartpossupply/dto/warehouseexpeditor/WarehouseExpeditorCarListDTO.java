package uz.uzkassa.smartpossupply.dto.warehouseexpeditor;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import uz.uzkassa.smartpossupply.dto.base.CommonCodeDTO;

import java.math.BigDecimal;

/**
 * Powered by: Shuxratjon Rayimjonov
 * Date: 29.11.2024
 */
@Getter
@Setter
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class WarehouseExpeditorCarListDTO {

    CommonCodeDTO car;

    BigDecimal totalSoldQty;

    BigDecimal totalQty;
}