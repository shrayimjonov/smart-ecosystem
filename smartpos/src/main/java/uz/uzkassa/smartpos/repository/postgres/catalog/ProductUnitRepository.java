package uz.uzkassa.smartpos.repository.postgres.catalog;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import uz.uzkassa.smartpos.domain.postgres.catalog.ProductUnit;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface ProductUnitRepository extends JpaRepository<ProductUnit, Long>, JpaSpecificationExecutor<ProductUnit> {

    List<ProductUnit> findAllByBranchIdOrderBySorder(Long branchId);

    List<ProductUnit> findAllByBranchIdAndLastModifiedDateAfterOrderBySorder(Long branchId, LocalDateTime from);

    List<ProductUnit> findAllByBranchIdAndProductIdOrderBySorder(Long branchId, Long productId);

    @Query("select coefficient from ProductUnit where branchId=?1 and productId=?2 and unitId=?3")
    Optional<Double> findCoefficient(Long branchId, Long productId, Long unitId);

    List<ProductUnit> findAllByBranchIdAndCityIdAndProductIdIn(Long branchId, Long cityId, List<Long> productIds);

    @Modifying
    @Query("delete from ProductUnit where (branchId , cityId) = (select id, cityKeyId from Branch where id = :branchId)")
    void deleteProductUnitByBranchId(@Param("branchId") Long branchId);


}
