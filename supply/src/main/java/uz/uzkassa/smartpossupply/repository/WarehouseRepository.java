package uz.uzkassa.smartpossupply.repository;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import uz.uzkassa.smartpossupply.domain.warehouse.Warehouse;
import uz.uzkassa.smartpossupply.enums.CommonStatus;

import java.util.List;
import java.util.Optional;
import java.util.Set;

@Repository
public interface WarehouseRepository extends BaseRepository<Warehouse, Long>, WarehouseRepositoryCustom {

    @EntityGraph(attributePaths = {"branch", "parent", "owner", "children"})
    @Query("select w from Warehouse w where w.deleted = false and w.id=:id")
    Optional<Warehouse> getDetail(@Param("id") Long id);

    Long countByBranch_CompanyIdAndStatusIs(Long companyId, CommonStatus status);

    boolean existsByBranchIdAndNameIgnoreCaseAndDeletedIsFalse(Long branchId, String name);

    boolean existsByBranchIdAndNameIgnoreCaseAndIdNotAndDeletedIsFalse(Long branchId, String name, Long id);

    boolean existsByIdAndBranchIdAndDeletedIsFalse(Long id, Long branchId);

    @Query(" select w.id from Warehouse w where w.branch.companyId=:companyId and w.deleted=false")
    List<Long> getAllIdsByCompanyId(@Param("companyId") Long companyId);

    @Query(" select w from Warehouse w where w.branch.companyId=:companyId and w.deleted=false and w.parentId is null")
    List<Warehouse> getAllByCompanyId(@Param("companyId") Long companyId);

    @Modifying
    @Query("update Warehouse set main=false where branchId=:branchId and id<>:id")
    void updateSetMainFalseByBranchId(@Param("branchId") Long branchId, @Param("id") Long id);

    @Query("select w.id from Warehouse w where w.deleted = false and w.main = true and w.branchId=:branchId")
    Long getMainWarehouseId(@Param("branchId") Long branchId);

    boolean existsByBranch_CompanyIdAndTruckModelAndTruckRegNo(Long companyId, String truckModel, String truckRegNo);

    boolean existsByBranch_CompanyIdAndTruckModelAndTruckRegNoAndIdNot(Long companyId, String truckModel, String truckRegNo, Long id);

    Optional<Warehouse> findFirstByVehicleIdAndParentIdAndDeletedIsFalse(Long vehicleId, Long parentId);

    Optional<Warehouse> findFirstByTruckRegNoAndTruckModelAndParentIdAndDeletedIsFalse(String truckRegNo, String truckModel, Long parentId);

    List<Warehouse> findAllByVehicleIdAndParentIdNotInAndDeletedIsFalse(Long vehicleId, List<Long> ids);

    List<Warehouse> findAllByBranchIdInAndStatusAndDeletedIsFalse(Set<Long> branchIds, CommonStatus status);

    @Query(""" 
            select w.id from Warehouse w where w.deleted = false and w.status = 'ACTIVE' and w.branchId in (:branchIds)
            """)
    List<Long> findActiveIdsByBranchIdIn(@Param("branchIds") Set<Long> branchIds);

}
