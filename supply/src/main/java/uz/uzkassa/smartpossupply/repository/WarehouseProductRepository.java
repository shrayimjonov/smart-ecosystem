package uz.uzkassa.smartpossupply.repository;

import jakarta.persistence.LockModeType;
import jakarta.persistence.QueryHint;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.QueryHints;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import uz.uzkassa.smartpossupply.domain.warehouse.WarehouseProduct;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Repository
public interface WarehouseProductRepository extends BaseRepository<WarehouseProduct, Long>, WarehouseProductRepositoryCustom {

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @QueryHints({@QueryHint(name = "javax.persistence.lock.timeout", value = "5000")})
    Optional<WarehouseProduct> findByWarehouseIdAndProductIdAndUnitId(Long warehouseId, Long productId, Long unitId);

    @Query(
            "SELECT SUM(pw.qty) FROM WarehouseProduct pw where pw.warehouseId=:warehouseId and pw.productId=:productId and pw.unitId=:unitId"
    )
    BigDecimal getQty(@Param("warehouseId") Long warehouseId, @Param("productId") Long productId, @Param("unitId") Long unitId);

    @Query(
            "SELECT SUM(pw.reservationQty) FROM WarehouseProduct pw where pw.warehouseId=:warehouseId and pw.productId=:productId and pw.unitId=:unitId"
    )
    BigDecimal getReservationQty(@Param("warehouseId") Long warehouseId, @Param("productId") Long productId, @Param("unitId") Long unitId);

    Optional<WarehouseProduct> findFirstByProductIdAndDeletedIsFalse(Long productId);

    @EntityGraph(attributePaths = {"warehouse", "warehouse.branch", "product"})
    List<WarehouseProduct> findAllByProductIdAndDeletedIsFalse(Long productId);

    @EntityGraph(attributePaths = {"product"})
    @Query("select w from WarehouseProduct w where w.warehouse.branchId = :branchId and w.warehouseId = :warehouseId and w.deleted = false")
    List<WarehouseProduct> findAllByBranchIdAndWarehouseIdAndDeletedIsFalse(@Param("branchId") Long branchId, @Param("warehouseId") Long warehouseId);

    @Query("select w.id from WarehouseProduct w where w.deleted = false")
    List<Long> findAllIdsByDeletedIsFalse();

    @EntityGraph(attributePaths = {"warehouse", "product"})
    @Query("select wp from WarehouseProduct wp where wp.id=:id")
    Optional<WarehouseProduct> findByIdForMaterialReport(@Param("id") Long id);

    List<WarehouseProduct> findAllByWarehouseIdInAndDeletedFalse(List<Long> warehouseIds);

}
