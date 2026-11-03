package uz.uzkassa.horeca.repository;

import com.amazonaws.event.ProgressEventFilter;
import jakarta.persistence.NamedEntityGraph;
import jakarta.validation.constraints.NotNull;
import org.springframework.data.domain.Limit;
import org.springframework.data.domain.Sort;
import uz.uzkassa.horeca.domain.warehouse.WarehouseItem;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Collection;
import java.util.List;
import java.util.Optional;


public interface WarehouseItemRepository extends BaseRepository<WarehouseItem, Long>, WarehouseItemRepositoryCustom {
    Optional<WarehouseItem> findByWarehouseIdAndProductIdAndUnitId(Long warehouseId, Long productId, Long unitId);

    Optional<WarehouseItem> findByWarehouseIdAndProductIdAndUnitIdAndCreatedDateAndPrice(Long warehouseId, Long productId, Long unitId, LocalDate createdDate, BigDecimal price);

    Optional<WarehouseItem> findByWarehouseIdAndProductIdAndUnitIdAndCreatedDateAndPriceAndQtyGreaterThanEqual(Long warehouseId, Long productId, Long unitId, LocalDate createdDate, BigDecimal price, BigDecimal qty);

//    @Query(value = "select count(i.id) > 0 from WarehouseItem i where i.warehouseId=:warehouseId and i.qty > 0")
    boolean existsByWarehouseIdAndQtyIsGreaterThan(@NotNull Long warehouseId, BigDecimal qty);

    List<WarehouseItem> findAllByWarehouseIdAndProductIdAndUnitIdAndQtyGreaterThan(@NotNull Long warehouseId, Long productId, @NotNull Long unitId, BigDecimal qtyIsGreaterThan);

}
