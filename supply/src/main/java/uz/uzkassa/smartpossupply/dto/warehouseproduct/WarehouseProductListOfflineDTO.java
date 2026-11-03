package uz.uzkassa.smartpossupply.dto.warehouseproduct;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

import java.math.BigDecimal;


@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
@AllArgsConstructor
@RequiredArgsConstructor
public class WarehouseProductListOfflineDTO {

    Long id;

    Long productId;

    BigDecimal qty;

    BigDecimal reservationQty;


}
