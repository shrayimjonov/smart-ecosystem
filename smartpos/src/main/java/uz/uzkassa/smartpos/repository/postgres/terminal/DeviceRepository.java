package uz.uzkassa.smartpos.repository.postgres.terminal;


import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import uz.uzkassa.smartpos.domain.postgres.terminal.Device;
import uz.uzkassa.smartpos.enums.DeviceType;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface DeviceRepository extends JpaRepository<Device, Long>, JpaSpecificationExecutor<Device>, DeviceRepositoryCustom {
    List<Device> findAllByCompanyId(Long companyId);

    List<Device> findAllByBranchId(Long branchId);

    List<Device> findAllBySerialNumber(String serialNumber);

    Optional<Device> findFirstBySerialNumber(String serialNumber);

    List<Device> findAllByBranchIdAndTypeIn(Long branchId, List<DeviceType> deviceTypes);

    @Query(value = "select count(*) from device where company_id = :companyId and type = 'FISCAL'", nativeQuery = true)
    int companyTerminalsCount(Long companyId);

    List<Device> findAllByCompanyIdInAndType(Collection<Long> companyIds, DeviceType deviceType);

    Optional<Device> findFirstBySerialNumberAndType(String serialNumber, DeviceType deviceType);

    Optional<Device> findFirstByBranchIdAndType(Long branchId, DeviceType deviceType);

    List<Device> findAllByCompanyIdAndType(Long companyId, DeviceType deviceType);

    List<Device> findAllByBranchIdAndType(Long branchId, DeviceType deviceType);

    List<Device> findAllByBranchIdInAndType(Collection<Long> branchIds, DeviceType deviceType);
}