package uz.uzkassa.smartpos.repository.postgres.catalog;

import jakarta.validation.constraints.NotNull;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import uz.uzkassa.smartpos.domain.postgres.catalog.ProductBranch;
import uz.uzkassa.smartpos.domain.postgres.catalog.ProductBranchId;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

@Repository
public interface ProductBranchRepository extends JpaRepository<ProductBranch, ProductBranchId>, ProductBranchRepositoryCustom {
    List<ProductBranch> findByCityIdAndBranchIdAndProductIdInAndDeletedFalse(Long cityId, Long branchId, Collection<Long> productIds);

    Optional<ProductBranch> findFirstByBarcodeAndBranchIdAndCityIdAndDeletedIsFalse(String barcode, Long branchId, Long cityId);

    List<ProductBranch> findAllByBarcodeInAndBranchIdAndCityIdAndDeletedIsFalse(List<String> barcodes, Long branchId, Long cityId);

    boolean existsProductBranchByCityIdAndBranchIdAndLastModifiedDateAfter(Long cityId, Long branchId, LocalDateTime lastSyncDate);

    @Modifying
    @Query("update ProductBranch set committentTin=:committentTin, lastModifiedDate=current_timestamp where cityId=:cityId and branchId=:branchId and categoryId=:categoryId")
    void categoryCommittentUpdated(@Param("cityId") Long cityId, @Param("categoryId") Long categoryId, @Param("branchId") Long branchId, @Param("committentTin") String committentTin);

    @Modifying
    @Query("update ProductBranch set committentTin=:committentTin, lastModifiedDate=current_timestamp where cityId=:cityId and branchId=:branchId")
    void branchCommittentUpdated(@Param("cityId") Long cityId, @Param("branchId") Long branchId, @Param("committentTin") String committentTin);

    @Modifying
    @Query("update ProductBranch set origin=:origin, lastModifiedDate=current_timestamp where cityId=:cityId and branchId=:branchId and categoryId=:categoryId")
    void categoryProductOriginUpdated(@Param("cityId") Long cityId, @Param("categoryId") Long categoryId, @Param("branchId") Long branchId, @Param("origin") Integer origin);

    @Modifying
    @Query("update ProductBranch set origin=:origin, lastModifiedDate=current_timestamp where cityId=:cityId and branchId=:branchId")
    void branchProductOriginUpdated(@Param("cityId") Long cityId, @Param("branchId") Long branchId, @Param("origin") Integer origin);

    @Modifying
    @Query("update ProductBranch set vatRate=:vatRate, lastModifiedDate=current_timestamp where cityId=:cityId and branchId=:branchId and categoryId=:categoryId")
    void categoryVatUpdate(@Param("cityId") Long cityId, @Param("categoryId") Long categoryId, @Param("branchId") Long branchId, @Param("vatRate") BigDecimal vatRate);

    @Modifying
    @Query("update ProductBranch set vatRate=:vatRate, lastModifiedDate=current_timestamp where cityId=:cityId and branchId=:branchId")
    void branchVatUpdate(@Param("cityId") Long cityId, @Param("branchId") Long branchId, @Param("vatRate") BigDecimal vatRate);

    @Modifying
    @Query("update ProductBranch set hasMark=:hasMark, lastModifiedDate=current_timestamp where cityId=:cityId and branchId=:branchId and categoryId=:categoryId")
    void categoryMarkUpdate(@Param("cityId") Long cityId, @Param("categoryId") Long categoryId, @Param("branchId") Long branchId, @Param("hasMark") boolean hasMark);

    @Modifying
    @Query("update ProductBranch set hasMark=:hasMark, lastModifiedDate=current_timestamp where cityId=:cityId and branchId=:branchId")
    void branchMarkUpdate(@Param("cityId") Long cityId, @Param("branchId") Long branchId, @Param("hasMark") boolean hasMark);

    List<ProductBranch> findAllByBranchIdAndCityIdAndDeletedIsFalseAndProductIdIn(Long branchId, Long branchCityId, List<Long> productIds);

    Page<ProductBranch> findAllByCityIdAndCategoryIdInAndBranchIdAndDeletedIsFalseOrderByProductId(Long cityKeyId, List<Long> categoryId, Long branchId, Pageable pageable);

    @Transactional
    @Modifying
    @Query(value = "update product_branch p set deleted = true, last_modified_date = now(), last_modified_by = ?1 where p.deleted is false and p.branch_id = ?2 and p.city_id = ?3 and p.category_id in (?4)", nativeQuery = true)
    void deleteByBranchIdAndCityIdAndCategoryIdIn(String login, Long branchId, Long cityId, Collection<Long> categoryIds);

    Page<ProductBranch> findAllByBranchIdAndCityIdAndCategoryIdAndDeletedIsFalseOrderByProductId(Long branchId, Long cityId, Long categoryId, Pageable pageable);

    @Modifying
    @Query(value = "delete from product_branch where deleted and last_modified_date <= now() - interval '1 week' and city_id = :cityId", nativeQuery = true)
    void cleanup(@Param("cityId") Long cityId);

    List<ProductBranch> findAllByBranchIdAndCityIdAndNameInAndDeletedIsFalse(Long branchId, Long cityId, Collection<String> names);

    List<ProductBranch> findAllByBranchIdAndCityIdAndVatBarcodeInAndDeletedIsFalse(Long branchId, Long cityId, Collection<String> vatBarcodes);

    @Query(value = "select pb.productId from ProductBranch pb where pb.branchId = :branchId and pb.cityId = :cityId and pb.deleted = false and pb.categoryId in (:categoryIds)")
    List<Long> findAllByBranchIdAndCityIdAndCategoryIdInAndDeletedIsFalse(Long branchId, Long cityId, List<Long> categoryIds);

    List<ProductBranch> findByVatBarcodeInAndBranchIdAndCityIdAndDeletedIsFalse(Collection<String> vatBarcodes, Long branchId, Long cityId);

    @Transactional
    @Modifying
    @Query(value = "update ProductBranch p set p.deleted = true, p.lastModifiedDate = current timestamp where p.vatBarcode = :vatBarcode and p.deleted = false")
    void deleteAllByVatBarcodeAndDeletedFalse(String vatBarcode);

    void deleteAllByBranchIdAndCityId(@NotNull Long branchId, Long cityId);
}
