package uz.uzkassa.smartpossupply.dto.company;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import uz.uzkassa.smartpossupply.dto.warehouse.WarehouseMarketplaceDTO;
import uz.uzkassa.smartpossupply.enums.DeliveryType;

import java.util.ArrayList;
import java.util.List;

/**
 * Powered by: Shuxratjon Rayimjonov
 * Date: 28.08.2023
 */
@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
@NoArgsConstructor
public class CompanyDeliveryTypeDetailDTO {

    DeliveryType deliveryType;

    Integer deliveryDaysFrom;

    Integer deliveryDaysTo;

    List<WarehouseMarketplaceDTO> warehouses = new ArrayList<>();

    boolean selectAll;

    public void add(WarehouseMarketplaceDTO warehouse) {
        this.warehouses.add(warehouse);
    }
}
