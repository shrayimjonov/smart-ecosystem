package uz.uzkassa.cts.repository;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import uz.uzkassa.cts.domain.OfdApplication;
import uz.uzkassa.cts.enums.ApplicationType;
import uz.uzkassa.cts.enums.OfdStatus;
import uz.uzkassa.cts.repository.custom.OfdApplicationRepositoryCustom;

import java.util.List;
import java.util.Optional;

@Repository
public interface OfdApplicationRepository extends BaseRepository<OfdApplication, Long>, OfdApplicationRepositoryCustom {

    Optional<OfdApplication> findFirstByOfdIdOrderByOfdIdDesc(Long ofdId);

    Optional<OfdApplication> findFirstByCustomerIdAndCustomerBranchIdAndDeviceIdAndApplicationType(Long customerId, Long customerBranchId, Long deviceId, ApplicationType applicationType);

    @Query("select a.ofdId from OfdApplication a where a.ofdStatus !=:ofdStatus order by a.id desc")
    List<Long> findAllByOfdIdsByStatusNot(@Param("ofdStatus") OfdStatus ofdStatus);
}
