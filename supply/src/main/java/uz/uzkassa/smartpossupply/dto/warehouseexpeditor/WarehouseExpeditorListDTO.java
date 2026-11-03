package uz.uzkassa.smartpossupply.dto.warehouseexpeditor;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import uz.uzkassa.smartpossupply.dto.base.CommonDTO;

import java.math.BigDecimal;
import java.util.List;

/**
 * Powered by: Shuxratjon Rayimjonov
 * Date: 29.11.2024
 */
@Getter
@Setter
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class WarehouseExpeditorListDTO {

    CommonDTO expeditor;

    List<CommonDTO> planRouteTemplates;

//    List<CommonDTO> cars;

    Long totalOrderCount;

    Long totalCompletedOrderCount;

    BigDecimal totalSoldQty;

    BigDecimal totalQty;
}