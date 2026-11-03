package uz.uzkassa.cts.repository;


import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import uz.uzkassa.cts.domain.inventory.WarehouseItem;

import java.util.Optional;

public interface WarehouseItemRepository extends BaseRepository<WarehouseItem, Long>, WarehouseItemRepositoryCustom {

    Optional<WarehouseItem> findByWarehouseIdAndItemIdAndUnitId(Long warehouseId, Long itemId, Long unitId);

    @Query(value = "SELECT wi.* FROM warehouse_item wi WHERE wi.warehouse_id=:warehouseId and wi.item_id=:itemId and wi.unit_id =:unitId and wi.variation_options @> :options", nativeQuery = true)
    WarehouseItem findFirstByWarehouseIdAndItemIdAndUnitIdAndOptions(@Param("warehouseId") Long warehouseId, @Param("itemId") Long itemId, @Param("unitId") Long unitId, @Param("options") long[] options);
}
