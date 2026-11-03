package uz.uzkassa.smartpos.repository.postgres.catalog;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import uz.uzkassa.smartpos.domain.postgres.catalog.ProductColorGroup;
import uz.uzkassa.smartpos.domain.postgres.catalog.ProductCompositeId;

import java.util.List;

@Repository
public interface ProductColorGroupRepository extends JpaRepository<ProductColorGroup, ProductCompositeId>, JpaSpecificationExecutor<ProductColorGroup>, ProductColorGroupRepositoryCustom {
    @EntityGraph(attributePaths = {"images", "images.file", "productItems"})
    List<ProductColorGroup> findAllByProductIdAndBranchIdAndDeletedIsFalse(Long productId, Long branchId);

    Page<ProductColorGroup> findAllByBranchIdAndDeletedIsFalse(Long branchId, Pageable pageable);

    @Transactional
    @Modifying
    @Query(value = "update product_color_group p set deleted = true where p.deleted is false and p.branch_id = ?1 and p.city_id = ?2 and p.product_id in (?3)", nativeQuery = true)
    void deleteByBranchIdAndCityIdAndProductIdIn(Long branchId, Long cityId, List<Long> productIds);


    @Modifying
    @Query(value = "update product_color_group p set deleted = true where p.deleted is false and p.product_id = ?1 and p.branch_id = ?2", nativeQuery = true)
    void deleteByProductIdAndBranchId(Long productId, Long branchId);
}
