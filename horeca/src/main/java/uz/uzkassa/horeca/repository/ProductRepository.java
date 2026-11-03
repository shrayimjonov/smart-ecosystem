package uz.uzkassa.horeca.repository;

import uz.uzkassa.horeca.domain.product.Product;

import java.util.List;
import java.util.Optional;
import java.util.UUID;


public interface ProductRepository extends BaseRepository<Product, Long>, ProductRepositoryCustom {
    boolean existsByCategoryIdAndDeletedIsFalse(Long categoryId);

    boolean existsByBranchIdAndNameIgnoreCaseAndDeletedIsFalse(Long branchId, String name);

    Optional<Product> findFirstByBranchIdAndNameIgnoreCaseAndDeletedIsFalse(Long branchId, String name);

    boolean existsByBranchIdAndNameIgnoreCaseAndIdNotAndDeletedIsFalse(Long branchId, String name, Long id);

    Optional<Product> findFirstByBranchIdAndBarcodeAndDeletedIsFalse(Long branchId, String barcode);

    Optional<Product> findFirstByBranchIdAndCatalogCodeAndDeletedIsFalse(Long branchId, String catalogCode);

    Optional<Product> findFirstByCategoryIdAndDeletedIsFalse(Long categoryId);

    Optional<Product> findFirstByBranchIdAndCategoryIdAndDeletedIsFalse(Long branchId, Long categoryId);

    List<Product> findAllByCategoryIdAndDeletedIsFalse(Long categoryId);

    Optional<Product> findFirstByUniqueIdAndDeletedIsFalseAndBranchId(UUID uniqueId, Long branchId);

    List<Product> findAllByBranchIdAndDeletedIsFalse(Long branchId);
}
