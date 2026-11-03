package uz.uzkassa.smartpossupply.repository;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import uz.uzkassa.smartpossupply.domain.CompanyCommittentWarehouse;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

/**
 * Powered by: Shuxratjon Rayimjonov
 * Date: 03.01.2024
 */
@Repository
public interface CompanyCommittentWarehouseRepository extends BaseRepository<CompanyCommittentWarehouse, Long>, CompanyCommittentWarehouseRepositoryCustom {

    Optional<CompanyCommittentWarehouse> findFirstByProductIdAndWarehouseIdAndCompanyCommittentId(Long productId, Long warehouseId, Long companyCommittentId);

    @Query("select sum(ccw.qty) from CompanyCommittentWarehouse ccw where ccw.warehouseId=:warehouseId and ccw.companyCommittentId=:companyCommittentId")
    BigDecimal getQty(@Param("warehouseId") Long warehouseId, @Param("companyCommittentId") Long companyCommittentId);

    List<CompanyCommittentWarehouse> findAllByCompanyCommittentId(Long companyCommittentId);
}
