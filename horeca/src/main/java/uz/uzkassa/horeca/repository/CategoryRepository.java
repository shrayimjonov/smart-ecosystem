package uz.uzkassa.horeca.repository;

import uz.uzkassa.horeca.domain.Category;
import uz.uzkassa.horeca.enums.CategoryType;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface CategoryRepository extends BaseRepository<Category, Long>, CategoryRepositoryCustom {
    Optional<Category> findFirstByBranchIdAndCatalogCodeAndDeletedIsFalse(Long branchId, String catalogCode);

    Optional<Category> findFirstByBranchIdAndCategoryTypeAndDeletedIsFalse(Long branchId, CategoryType categoryType);

    Optional<Category> findFirstByCategoryTypeAndDeletedIsFalse(CategoryType categoryType);

    boolean existsByNameIgnoreCaseAndBranchIdAndDeletedIsFalse(String name, Long branchId);

    boolean existsByNameIgnoreCaseAndBranchIdAndDeletedIsFalseAndIdNot(String name, Long branchId, Long id);

    List<Category> findAllByBranchIdAndDeletedIsFalseAndParentIdIsNullAndCategoryType(Long branchId, CategoryType categoryType);

    List<Category> findAllByBranchIdAndDeletedIsFalseAndParentIdIsNull(Long branchId);

    Optional<Category> findFirstByNameIgnoreCaseAndBranchIdAndDeletedIsFalse(String name, Long branchId);

    Optional<Category> findFirstByUniqueIdAndDeletedIsFalseAndBranchId(UUID uniqueId, Long branchId);
}
