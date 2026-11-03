package uz.uzkassa.smartpos.repository.postgres.catalog;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import uz.uzkassa.smartpos.domain.postgres.catalog.ProductCompositeId;
import uz.uzkassa.smartpos.domain.postgres.catalog.ProductItem;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProductItemRepository extends JpaRepository<ProductItem, ProductCompositeId>, ProductItemRepositoryCustom {
    Optional<ProductItem> findOneByBarcode(String barcode);

    @Query("select p from ProductItem p join fetch p.colorGroup c join fetch c.images where p.barcode like :barcode and p.branchId = :branchId order by p.name limit 10")
    List<ProductItem> findAllLikeBarcode(String barcode, Long branchId);


    @Modifying
    @Query(value = "update product_item p set deleted = true where p.deleted is false and p.branch_id = ?1 and p.city_id = ?2 and p.product_id in (?3)", nativeQuery = true)
    void deleteByAndBranchIdAndCityIdAndProductIdIn(Long branchId, Long cityId, List<Long> productIds);

    @Modifying
    @Query(value = "update product_item p set deleted = true where p.deleted is false and p.product_id = ?1 and p.branch_id = ?2", nativeQuery = true)
    void deleteByProductIdAndBranchId(Long id, Long branchId);
}
