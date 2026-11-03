package uz.uzkassa.cts.repository;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import uz.uzkassa.cts.domain.Fiscalization;
import uz.uzkassa.cts.enums.DeviceStatus;
import uz.uzkassa.cts.repository.custom.FiscalizationRepositoryCustom;

import java.util.List;
import java.util.Optional;

@Repository
public interface FiscalizationRepository extends BaseRepository<Fiscalization, Long>, FiscalizationRepositoryCustom {

    @Query("select count(f.id) from Fiscalization f where f.deleted=false and f.deviceId= ?1")
    Long totalByDeviceId(Long deviceId);

    Fiscalization findFirstByOfdApplicationId(Long applicationId);

    Optional<Fiscalization> findFirstByDeviceIdOrderByIdDesc(Long deviceId);

    Fiscalization findFirstByFiscalModuleIdOrderByIdDesc(Long fiscalModuleId);

    Fiscalization findFirstByCustomerIdAndCustomerBranchIdAndDeviceId(Long customerId, Long customerBranchId, Long deviceId);

    Fiscalization findFirstByDeviceIdAndFiscalModuleId(Long deviceId, Long fiscalModuleId);

    Fiscalization findFirstByCustomerIdAndDeviceIdAndFiscalModuleId(Long customerId, Long deviceId, Long fiscalModuleId);

    boolean existsByCustomerIdAndDeviceIdAndFiscalModuleId(Long customerId, Long deviceId, Long fiscalModuleId);

    List<Fiscalization> findAllByFiscalModule_SerialNumber(String fiscalModuleSerialNumber);

    List<Fiscalization> findAllByDevice_SerialNumber(String deviceSerialNumber);

    //@Cacheable(cacheNames = CacheConstants.FISCALIZATION_CHECK_BY_HASH)
    @EntityGraph(attributePaths = {"device", "fiscalModule", "customer"})
    Fiscalization findFirstByHashKeyOrderByIdDesc(String hashKey);

    Fiscalization findFirstByCustomerIdAndDeviceId(Long customerId, Long deviceId);

    Fiscalization findFirstByCustomerIdAndCustomerBranchIdAndFiscalModuleId(Long customerId, Long customerBranchId, Long fiscalModuleId);

    Fiscalization findFirstByCustomerIdAndFiscalModuleId(Long customerId, Long fiscalModuleId);

    Fiscalization findFirstByCustomerIdAndCustomerBranchIdAndDeviceIdAndFiscalModuleId(Long customerId, Long customerBranchId, Long deviceId, Long fiscalModuleId);

    Fiscalization findFirstByCustomerIdAndCustomerBranchIdAndStatus(Long customerId, Long customerBranchId, DeviceStatus status);

    Fiscalization findFirstByCustomerIdAndStatus(Long customerId, DeviceStatus status);

    List<Fiscalization> findAllByCustomerIdAndCustomerBranchIdAndStatus(Long customerId, Long customerBranchId, DeviceStatus status);

    @Query("select f from Fiscalization f " +
        " inner join f.ofdApplication ofdApp " +
        " inner join f.customer c " +
        " where c.tin = ?1 " +
        " and ofdApp.ofdStatus not in ('ACCEPT','REJECT') " +
        " and f.deleted=false and c.deleted=false and f.paid=false " +
        " and ofdApp.deleted=false and ofdApp.applicationType='REGISTER' ")
    List<Fiscalization> findByCustomerTin(String tin);

    @Query("select count(f.id) from Fiscalization f where f.customer.tin = ?1 and  f.deleted=false and f.customer.deleted=false ")
    Long totalByCustomerTin(String tin);

    @Query("select count(f.id) from Fiscalization f where f.deleted=false and f.ofdApplicationId is not null and f.status=?1 and f.syncedSmartpos=false")
    Long getCountByStatusForSmartpos(DeviceStatus status);

    @Query("select f from Fiscalization f where f.deleted=false and f.ofdApplicationId is not null and f.customerId = ?1 and f.status= ?2 and f.syncedSmartpos=false")
    List<Fiscalization> findByCustomerIdAndStatusForSmartpos(Long customerId, DeviceStatus status);

    Optional<Fiscalization> findFirstByDeviceIdAndStatusInAndDeletedIsFalse(Long deviceId, List<DeviceStatus> status);

    Long countAllByBranchId(Long branchId);

    Long countAllByCustomerBranchId(Long customerBranchId);
}
