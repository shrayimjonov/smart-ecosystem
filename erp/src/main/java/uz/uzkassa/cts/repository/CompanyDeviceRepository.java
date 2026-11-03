package uz.uzkassa.cts.repository;

import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import uz.uzkassa.cts.domain.CompanyDevice;
import uz.uzkassa.cts.repository.custom.CompanyDeviceRepositoryCustom;

import java.util.List;
import java.util.Optional;

@Repository
public interface CompanyDeviceRepository extends BaseRepository<CompanyDevice, Long>, CompanyDeviceRepositoryCustom {

    List<CompanyDevice> findAllByDeviceId(Long deviceId);

    CompanyDevice findFirstByCompanyIdAndDeviceId(Long companyId, Long deviceId);

    @Query("select count(cd.id) from CompanyDevice cd where cd.deleted=false and cd.companyId=?1 and cd.deviceId= ?2")
    Long totalByDeviceId(Long companyId, Long deviceId);

    Optional<CompanyDevice> findFirstByDeviceIdOrderByIdDesc(Long deviceId);
}
