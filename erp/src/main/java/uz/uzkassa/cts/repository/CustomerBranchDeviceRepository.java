package uz.uzkassa.cts.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import uz.uzkassa.cts.domain.Customer;
import uz.uzkassa.cts.domain.CustomerBranchDevice;
import uz.uzkassa.cts.dto.customerbranchdevicechangetariff.CustomerBranchDeviceTariffDto;
import uz.uzkassa.cts.dto.dashboard.response.DashboardStatistics;
import uz.uzkassa.cts.dto.dashboard.response.DashboardStatsByDistrictDto;
import uz.uzkassa.cts.dto.dashboard.response.DashboardStatsByRegionDto;
import uz.uzkassa.cts.enums.DeviceType;
import uz.uzkassa.cts.repository.custom.CustomerBranchDeviceRepositoryCustom;

import java.util.List;
import java.util.Optional;

@Repository
public interface CustomerBranchDeviceRepository extends BaseRepository<CustomerBranchDevice, Long>, CustomerBranchDeviceRepositoryCustom {

    @Override
    @EntityGraph(attributePaths = {"customerBranch", "customerBranch.customer"})
    Page<CustomerBranchDevice> findAll(Pageable pageable);

    Optional<CustomerBranchDevice> findFirstByDeviceIdOrderByIdDesc(Long deviceId);

    boolean existsByDevice_SerialNumberAndDeletedIsFalse(String serialNumber);

    @Query("select cbd from CustomerBranchDevice cbd " +
            " inner join cbd.customerBranch cb" +
            " inner join cb.customer c" +
            " inner join cbd.device d where c.tin=?1 and d.serialNumber = ?2")
    Optional<CustomerBranchDevice> findOneByTinAndDeviceSerialNumber(String tin, String serialNumber);

    @Query("select count(cbd.id) from CustomerBranchDevice cbd where cbd.deleted=false and cbd.deviceId= ?1")
    Long totalByDeviceId(Long deviceId);

    @Query("select count(cbd.id) from CustomerBranchDevice cbd where cbd.deleted=false and cbd.customerBranchId= ?1")
    Long totalByCustomerBranchId(Long customerBranchId);

    CustomerBranchDevice findFirstByCustomerBranchIdAndDeviceId(Long customerBranchId, Long deviceId);

    CustomerBranchDevice findFirstByCustomerBranch_CustomerIdAndDeviceId(Long customerId, Long deviceId);

    @Query("select c from CustomerBranchDevice cbd " +
            " inner join cbd.customerBranch cb" +
            " inner join cb.customer c" +
            " inner join cbd.device d where d.serialNumber = ?1")
    Optional<Customer> findOneByDeviceSerialNumber(String serialNumber);

    @Query("select cbd from CustomerBranchDevice cbd " +
            " inner join fetch cbd.customerBranch cb" +
            " inner join fetch cb.customer c" +
            " inner join fetch cbd.tariff t" +
            " inner join fetch cbd.device d where c.id = ?1")
    List<CustomerBranchDevice> findByCustomerId(Long customerId);

    @Query("select cbd from CustomerBranchDevice cbd " +
            " inner join fetch cbd.customerBranch cb" +
            " inner join fetch cb.customer c" +
            " inner join fetch cbd.device d where c.id = ?1")
    List<CustomerBranchDevice> findLaucherBankTypeChangeByCustomerId(Long customerId);

    @Query("select new uz.uzkassa.cts.dto.customerbranchdevicechangetariff.CustomerBranchDeviceTariffDto(" +
            " c.id, c.tin, d.serialNumber, d.id, t.id, t.name) from CustomerBranchDevice cbd " +
            " inner join cbd.customerBranch cb" +
            " inner join cb.customer c" +
            " inner join cbd.tariff t " +
            " inner join cbd.device d where c.id=?1 and d.id = ?2")
    Optional<CustomerBranchDeviceTariffDto> findOneByCustomerIdAndDeviceId(Long customerId, Long deviceId);

    @Query("select count(cbd) from CustomerBranchDevice cbd " +
            " inner join cbd.customerBranch cb" +
            " inner join cb.customer c where c.id = ?1")
    Long countByCustomerId(Long customerId);


    @Query("select new uz.uzkassa.cts.dto.dashboard.response.DashboardStatistics(COUNT(cbd.customerBranchId), t.name, cbd.tariffId,  cbd.status)" +
            " from CustomerBranchDevice cbd " +
            " left join cbd.tariff t" +
            " group by cbd.tariffId, t.name, cbd.status")
    List<DashboardStatistics> getStatisticsByTariffAndStatus();


    @Query("select new uz.uzkassa.cts.dto.dashboard.response.DashboardStatsByRegionDto(r.nameRu, r.id, t.name, t.id, cbd.status, COUNT(cbd.id)) " +
            " from CustomerBranchDevice cbd " +
            " inner join cbd.customerBranch cb " +
            " inner join cb.address.region r " +
            " left join cbd.tariff t " +
            " group by t.id, t.name, cbd.status, r.id, r.nameRu " +
            " order by r.nameRu,  t.id, cbd.status ")
    List<DashboardStatsByRegionDto> getStatsByRegions();

    @Query("select new uz.uzkassa.cts.dto.dashboard.response.DashboardStatsByDistrictDto(r.nameRu, r.id, t.name, t.id, cbd.status, COUNT(cbd.id), d.nameRu, d.id) " +
            " from CustomerBranchDevice cbd " +
            " inner join cbd.customerBranch cb " +
            " inner join cb.address.region r " +
            " inner join cb.address.district d " +
            " left join cbd.tariff t " +
            " where r.id = ?1 " +
            " group by t.id, t.name, cbd.status, r.id, r.nameRu, d.id, d.nameRu " +
            " order by r.nameRu, d.id,  t.id, cbd.status,  d.nameRu")
    List<DashboardStatsByDistrictDto> getStatsByRegion(Long id);

    @Query(value = "select * from customer_branch_device cbd where cbd.id=?1", nativeQuery = true)
    CustomerBranchDevice findByIdWithDeleted(Long id);

    @Query(value = """
            select cbd.*\s
             from customer_branch_device cbd\s
             inner join customer_branch cb on cbd.customer_branch_id = cb.id\s
             inner join customer c on cb.customer_id = c.id\s
             inner join device d on cbd.device_id = d.id\s
            where cbd.deleted = false and cbd.status in ('FISCAL_ACTIVE', 'BLOCKED', 'UN_BLOCKED') and c.tin = :tin and d.serial_number = :serialNumber\s
            """, nativeQuery = true)
    Optional<CustomerBranchDevice> findByTinAndDeviceSerialNumber(@Param("tin") String tin, @Param("serialNumber") String serialNumber);

    @Query(value = """
            select cbd.*\s
             from customer_branch_device cbd\s
             inner join customer_branch cb on cbd.customer_branch_id = cb.id\s
             inner join customer c on cb.customer_id = c.id\s
             inner join device d on cbd.device_id = d.id\s
            where cbd.deleted = false and cbd.status in ('FISCAL_ACTIVE', 'BLOCKED', 'UN_BLOCKED') and c.old_tin = :oldTin and d.serial_number = :serialNumber\s
            """, nativeQuery = true)
    Optional<CustomerBranchDevice> findByOldTinAndDeviceSerialNumber(@Param("oldTin") String oldTin, @Param("serialNumber") String serialNumber);

    boolean existsByIdAndDeviceTypeIn(Long id, List<DeviceType> types);

    @Query("select count(cbd.id) from CustomerBranchDevice cbd " +
            "join CustomerBranch cb on cbd.customerBranchId = cb.id " +
            "where cb.customerId = :customerId")
    Long countAllByCustomerId(@Param("customerId") Long customerId);

    Long countAllByCustomerBranchId(Long customerBranchId);

    boolean existsByCustomerBranchIdAndDeviceType(Long customerBranchId, DeviceType deviceType);

    List<CustomerBranchDevice> findAllByCustomerBranch_CustomerIdAndDeviceTypeNotLikeAndDeletedIsFalse(Long customerId, DeviceType type);
}
