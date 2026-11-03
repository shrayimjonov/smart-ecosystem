package uz.uzkassa.smartpossupply.repository;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import uz.uzkassa.smartpossupply.domain.product.ProductContractWarehouse;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Repository
public interface ProductContractWarehouseRepository extends BaseRepository<ProductContractWarehouse, Long>, ProductContractWarehouseRepositoryCustom {

    Optional<ProductContractWarehouse> findFirstByWarehouseIdAndProductContractId(Long warehouseId, Long productContractId);

    @Query("select sum(pcw.qty) from ProductContractWarehouse pcw where pcw.warehouseId=:warehouseId and pcw.productContractId=:productContractId")
    BigDecimal getQty(@Param("warehouseId") Long warehouseId, @Param("productContractId") Long productContractId);

    @Query("select sum(pcw.qty) from ProductContractWarehouse pcw where pcw.productContractId=:productContractId")
    BigDecimal getQty(@Param("productContractId") Long productContractId);

    @EntityGraph(attributePaths = {"productContract", "productContract.product", "warehouse"})
    List<ProductContractWarehouse> findAllByProductContractId(Long productContractId);

}
