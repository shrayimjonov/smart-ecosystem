package uz.uzkassa.smartpossupply.repository;

import jakarta.persistence.LockModeType;
import jakarta.persistence.QueryHint;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.QueryHints;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import uz.uzkassa.smartpossupply.domain.category.Category;
import uz.uzkassa.smartpossupply.enums.EnvironmentType;

import java.util.List;
import java.util.Optional;

@Repository
public interface CategoryRepository extends BaseRepository<Category, Long>, CategoryRepositoryCustom {

    @EntityGraph(attributePaths = {"branch", "parent"})
    @Query("SELECT c FROM Category c where c.id=:id")
    Optional<Category> getDetail(@Param("id") Long id);

    Optional<Category> findFirstByNameAndBranchIdAndDeletedIsFalse(String name, Long branchId);

    Optional<Category> findFirstByBranchIdAndDeletedIsFalse(Long branchId);

    boolean existsByBranchIdAndCatalogCodeAndEnvironmentTypeAndDeletedIsFalse(Long branchId, String catalogCode, EnvironmentType environmentType);

    boolean existsByBranchIdAndCatalogCodeAndEnvironmentTypeAndIdNotAndDeletedIsFalse(Long branchId, String catalogCode, EnvironmentType environmentType, Long id);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @QueryHints({@QueryHint(name = "javax.persistence.lock.timeout", value = "5000")})
    Optional<Category> findFirstByBranchIdAndCatalogCodeAndEnvironmentTypeAndDeletedIsFalse(Long branchId, String catalogCode, EnvironmentType environmentType);

    Optional<Category> findFirstByMainIsTrueAndBranchIdAndDeletedIsFalse(Long branchId);

    List<Category> findAllByBranchIdAndParentIdIsNullAndDeletedIsFalse(Long branchId);

    Optional<Category> findFirstByBranchIdAndNameIgnoreCaseAndDeletedIsFalse(Long branchId, String name);

    List<Category> findAllByIdInAndDeletedIsFalse(List<Long> ids);

    @Query("select c.id from Category c where c.deleted = false and c.parentId=:parentId")
    List<Long> findIdsByParentId(@Param("parentId") Long parentId);

}
