package uz.uzkassa.smartpossupply.repository;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import uz.uzkassa.smartpossupply.domain.Branch;
import uz.uzkassa.smartpossupply.enums.BranchType;
import uz.uzkassa.smartpossupply.enums.CommonStatus;

import java.util.List;
import java.util.Optional;

@Repository
public interface BranchRepository extends BaseRepository<Branch, Long>, BranchRepositoryCustom {

    @EntityGraph(attributePaths = {"owner", "address"})
    @Query("select b from Branch b where b.id=:id")
    Optional<Branch> getDetail(@Param("id") Long id);

    List<Branch> findAllByIdInAndDeletedIsFalse(List<Long> ids);

    @Query("select b.id from Branch b where b.deleted = false and b.company.erpId is not null")
    List<Long> getIdsToSyncErp();

    @Query("select b.id from Branch b where b.deleted = false and b.company.marketplaceId is not null")
    List<Long> getIdsToSyncMarketplace();

    @Query("SELECT b FROM Branch b WHERE b.companyId = :companyId and b.deleted = false and b.branchType =:branchType")
    @EntityGraph(attributePaths = {"address"})
    List<Branch> findAllToSync(@Param("companyId") Long companyId, @Param("branchType") BranchType branchType);

    @Query("select b.id from Branch b where b.deleted=false and b.companyId=:companyId and b.branchType=:branchType")
    Long getBranchIdByType(@Param("companyId") Long companyId, @Param("branchType") BranchType branchType);

    @Query("select b.id from Branch b where b.deleted=false and b.companyId=:companyId")
    List<Long> getBranchIdByCompanyId(@Param("companyId") Long companyId);

    @Query("select b.id from Branch b where b.deleted = false and b.main = true and b.companyId=:companyId")
    Long getMainBranchId(@Param("companyId") Long companyId);

    @Query("select b.id from Branch b where b.deleted = false and b.erpId=:erpId")
    Long getIdByErpId(@Param("erpId") Long erpId);

    List<Branch> findAllByCompanyIdAndAddress_RegionIdAndDeletedIsFalse(Long companyId, Long regionId);

    Long countByCompanyIdAndDeletedIsFalse(Long companyId);

    Long countAllByCompany_TinAndStatusAndDeletedIsFalse(String tin, CommonStatus status);

    Long countByCompanyIdAndStatusAndDeletedIsFalse(Long companyId, CommonStatus status);

    boolean existsByIdAndCompanyIdAndDeletedIsFalse(Long id, Long companyId);

    boolean existsByCompanyIdAndNameIgnoreCaseAndDeletedIsFalse(Long companyId, String name);

    boolean existsByCompanyIdAndNameIgnoreCaseAndIdNotAndDeletedIsFalse(Long companyId, String name, Long id);

    boolean existsByCompanyIdAndBranchNumberAndDeletedIsFalse(Long companyId, String branchNumber);

    boolean existsByCompanyIdAndBranchNumberAndIdNotAndDeletedIsFalse(Long companyId, String branchNumber, Long id);

    @Modifying
    @Query("update Branch set main=false where companyId=:companyId and id<>:id")
    void updateSetMainFalseByCompanyId(@Param("companyId") Long companyId, @Param("id") Long id);

    @Query("SELECT coalesce(max(b.incrementNumber), 0) FROM Branch b where b.companyId=:companyId and b.branchType=:branchType")
    Long getMaxIncrementNumber(@Param("companyId") Long companyId, @Param("branchType") BranchType branchType);

    Optional<Branch> findFirstByCompanyIdAndBranchNumberAndDeletedIsFalse(Long companyId, String branchNumber);
}
