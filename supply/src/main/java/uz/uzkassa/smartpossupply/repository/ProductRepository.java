package uz.uzkassa.smartpossupply.repository;

import jakarta.persistence.LockModeType;
import jakarta.persistence.QueryHint;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.QueryHints;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import uz.uzkassa.smartpossupply.domain.product.Product;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProductRepository extends BaseRepository<Product, Long>, ProductRepositoryCustom {

    @EntityGraph(attributePaths = {"company"})
    @Query("select p from Product p where p.id =:id")
    Optional<Product> getProductByIdWithCompany(@Param("id") Long id);

    Optional<Product> findFirstByVatBarcodeAndBranchIdAndDeletedIsFalse(String vatBarCode, Long branchId);

    Optional<Product> findFirstByVatBarcodeAndCompanyIdAndProductTypeIsNotNullAndDeletedIsFalse(String vatBarCode, Long companyId);

    Optional<Product> findFirstByBarcodeAndCompanyIdAndProductTypeIsNotNullAndDeletedIsFalse(String barcode, Long companyId);

    Optional<Product> findFirstByBarcodeAndBranchIdAndDeletedIsFalse(String barcode, Long branchId);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @QueryHints({@QueryHint(name = "javax.persistence.lock.timeout", value = "5000")})
    boolean existsByBarcodeAndBranchIdAndDeletedIsFalse(String barcode, Long branchId);

    Optional<Product> findFirstByBarcodeAndVatBarcodeAndBranchIdAndDeletedIsFalse(String barcode, String vatBarcode, Long branchId);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @QueryHints({@QueryHint(name = "javax.persistence.lock.timeout", value = "5000")})
    boolean existsByBarcodeAndVatBarcodeAndBranchIdAndDeletedIsFalse(String barcode, String vatBarcode, Long branchId);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @QueryHints({@QueryHint(name = "javax.persistence.lock.timeout", value = "5000")})
    boolean existsByBarcodeAndVatBarcodeAndBranchIdAndIdNotAndDeletedIsFalse(String barcode, String vatBarcode, Long branchId, Long id);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @QueryHints({@QueryHint(name = "javax.persistence.lock.timeout", value = "5000")})
    boolean existsByBarcodeAndBranchIdAndIdNotAndDeletedIsFalse(String barcode, Long branchId, Long id);

    Optional<Product> findFirstByBarcodeAndCompanyIdAndDeletedIsFalse(String barcode, Long companyId);

    Optional<Product> findFirstByTasnifIdAndBranchIdAndDeletedIsFalse(String tasnifId, Long branchId);

    List<Product> findAllByCompanyIdAndVatBarcodeIsNotNullAndDeletedIsFalse(Long companyId);

    boolean existsByCategoryIdAndDeletedIsFalse(Long categoryId);

    @Query(
            value = "select distinct p.vat_barcode from product p left join lgota l on l.catalog_code != p.vat_barcode where p.deleted = false and p.vat_barcode is not null and p.vat =:vat", nativeQuery = true)
    List<String> findAllByVat(@Param("vat") String vat);

    @Modifying
    @Query("update Product set parentId=null where parentId=:parentId and id!=:productId")
    void updateProductParent(@Param("parentId") Long parentId, @Param("productId") Long productId);

    List<Product> findAllByDiscountTrueAndDeletedIsFalse();

    List<Product> findAllByBranchIdAndDeletedIsFalse(Long branchId);

    List<Product> findAllByCategoryIdInAndDeletedIsFalse(List<Long> categoryIds);

    List<Product> findAllByBranchIdInAndBarcodeAndBarcodeIsNotNullAndDeletedIsFalse(List<Long> branchId, String barcode);
}
