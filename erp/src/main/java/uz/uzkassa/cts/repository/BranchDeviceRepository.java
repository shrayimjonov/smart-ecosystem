package uz.uzkassa.cts.repository;

import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import uz.uzkassa.cts.domain.BranchDevice;
import uz.uzkassa.cts.repository.custom.BranchDeviceRepositoryCustom;

import java.util.List;
import java.util.Optional;

@Repository
public interface BranchDeviceRepository extends BaseRepository<BranchDevice, Long>, BranchDeviceRepositoryCustom {

    List<BranchDevice> findAllByDeviceId(Long deviceId);

    BranchDevice findFirstByBranchIdAndDeviceId(Long branchId, Long deviceId);

    @Query(value = "select * from branch_device bd where bd.id=?1", nativeQuery = true)
    BranchDevice findByIdWithDeleted(Long id);

    @Query("select count(bd.id) from BranchDevice bd where bd.deleted=false and bd.deviceId= ?1")
    Long totalByDeviceId(Long deviceId);

    @Query(value = """
            SELECT count(bd.id) FROM branch_device bd
            INNER JOIN branch b ON b.id = bd.branch_id
            WHERE b.company_id =?1
            """, nativeQuery = true)
    Long countByCompanyId(Long companyId);

    Optional<BranchDevice> findFirstByDeviceIdOrderByIdDesc(Long deviceId);

    Long countAllByBranchId(Long branchId);
}
