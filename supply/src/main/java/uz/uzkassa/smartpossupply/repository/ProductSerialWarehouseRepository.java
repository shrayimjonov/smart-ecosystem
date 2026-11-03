package uz.uzkassa.smartpossupply.repository;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import uz.uzkassa.smartpossupply.domain.productserial.ProductSerialWarehouse;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

/**
 * Powered by: Shuxratjon Rayimjonov
 * Date: 03.01.2024
 */
@Repository
public interface ProductSerialWarehouseRepository extends BaseRepository<ProductSerialWarehouse, Long>, ProductSerialWarehouseRepositoryCustom {

    Optional<ProductSerialWarehouse> findFirstByWarehouseIdAndProductSerialId(Long warehouseId, Long productSerialId);

    @Query("select sum(psw.qty) from ProductSerialWarehouse psw where psw.productSerialId=:productSerialId")
    BigDecimal getQty(@Param("productSerialId") Long productSerialId);

    @Query("select sum(psw.qty) from ProductSerialWarehouse psw where psw.warehouseId=:warehouseId and psw.productSerialId=:productSerialId")
    BigDecimal getQty(@Param("warehouseId") Long warehouseId, @Param("productSerialId") Long productSerialId);

    @EntityGraph(attributePaths = {"productSerial", "warehouse"})
    List<ProductSerialWarehouse> findAllByProductSerialId(Long productId);
}
