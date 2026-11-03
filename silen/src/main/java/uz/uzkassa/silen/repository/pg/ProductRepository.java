package uz.uzkassa.silen.repository.pg;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import uz.uzkassa.silen.domain.Product;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

/**
 * Spring Data  repository for the Product entity.
 */
@SuppressWarnings("unused")
@Repository
public interface ProductRepository extends JpaRepository<Product, String>, ProductRepositoryCustom {
    Optional<Product> findFirstByBarcodeAndOrganizationIdAndDeletedIsFalse(String barcode, String organizationId);

    Optional<Product> findFirstByBarcodeAndCatalogCodeAndOrganizationIdAndDeletedIsFalse(String barcode, String catalogCode, String organizationId);

    @Query("select max(code) from Product")
    Optional<Integer> findMaxCode();

    List<Product> findAllByCodeIsNull();

    @Query("select o from Product o where (o.barcode=:barcode or o.parentBarcode=:barcode) and o.organizationId=:orgId")
    Optional<Product> getByBarcodeAndOrganizationIdAndDeletedIsFalse(@Param("barcode") String barcode, @Param("orgId") String orgId);

    @Query("select id from Product where organizationId=?1 and deleted is not true")
    List<String> findAllIds(String organizationId);

    boolean existsByBarcodeAndOrganizationIdAndDeletedIsFalse(String barcode, String orgId);

    List<Product> findAllByCatalogCodeEquals(String catalogCode);

    Optional<Product> findFirstByOrganizationIdAndCatalogCodeEqualsAndDeletedFalse(String organizationId, String catalogCode);

    List<Product> findAllByVatRate(BigDecimal vatRateCode);
}
