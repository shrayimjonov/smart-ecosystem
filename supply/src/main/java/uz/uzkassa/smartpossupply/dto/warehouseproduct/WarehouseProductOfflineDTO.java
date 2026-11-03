package uz.uzkassa.smartpossupply.dto.warehouseproduct;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

import java.util.List;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
@RequiredArgsConstructor
@AllArgsConstructor
public class WarehouseProductOfflineDTO {

    Long warehouseId;

    List<WarehouseProductListOfflineDTO> products;

}
