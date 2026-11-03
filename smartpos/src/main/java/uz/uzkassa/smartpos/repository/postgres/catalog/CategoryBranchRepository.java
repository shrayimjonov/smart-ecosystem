package uz.uzkassa.smartpos.repository.postgres.catalog;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import uz.uzkassa.smartpos.domain.postgres.catalog.CategoryBranch;
import uz.uzkassa.smartpos.domain.postgres.catalog.CategoryBranchId;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

@SuppressWarnings("unused")
@Repository
public interface CategoryBranchRepository extends JpaRepository<CategoryBranch, CategoryBranchId>, CategoryBranchRepositoryCustom {
    @Query(nativeQuery = true, value = "select nextval('category_id_sequence')")
    Long nextCategoryId();

    @Modifying
    @Query("update CategoryBranch set enabled=:enabled, lastModifiedDate=current_timestamp where branchId = :branchId and categoryId = :categoryId")
    void enableDisableCategory(@Param("enabled") boolean enabled, @Param("categoryId") Long categoryId, @Param("branchId") Long branchId);

    List<CategoryBranch> findAllByBranchIdAndEnabledIsTrue(Long branchId);

    boolean existsByBranchIdAndLastModifiedDateAfter(Long branchId, LocalDateTime date);

    List<CategoryBranch> findAllByEnabledIsTrueAndBranchIdAndNameIn(Long branchId, Collection<String> names);

    Optional<CategoryBranch> findFirstByCategoryIdAndBranchIdAndEnabledIsTrue(Long categoryId, Long branchId);

    Optional<CategoryBranch> findFirstByBranch_IdAndNameAndEnabledIsTrue(Long branchId, String categoryName);

    Optional<CategoryBranch> findByBranch_IdAndNameAndEnabledIsTrue(Long branchId, String categoryName);

    @Query(value = "select cb.categoryId from CategoryBranch cb where cb.enabled = true and cb.branchId = :branchId")
    List<Long> findCategoryIdsByBranch(@Param("branchId") Long branchId);


    @Query(value = "select cb from CategoryBranch cb where cb.enabled = true and cb.branchId = :branchId")
    List<CategoryBranch> findBranchCategories(@Param("branchId") Long branchId);

    @Modifying
    @Query("delete from CategoryBranch where branchId=:branchId")
    void deleteByBranchId(@Param("branchId") Long branchId);

    @Transactional
    @Modifying
    @Query(value = "update category_branch c set enabled = false, last_modified_date = now(), last_modified_by = ?1 where c.branch_id = ?2 and c.category_id in (?3)", nativeQuery = true)
    void disableCategoryBranches(String login, Long branchId, Collection<Long> categoryIds);

    @Modifying
    @Query(value = "insert into category_branch (branch_id, category_id, created_by, created_date, enabled, mxik_code, name, name_uz, parent_id) " +
            "select ?1, c.id, 'system', now(), true, c.mxik_code, c.name, c.name_uz, c.parent_id from category c where company_type = 'FASHION'", nativeQuery = true)
    void createFashionCatalog(Long branchId);
}
