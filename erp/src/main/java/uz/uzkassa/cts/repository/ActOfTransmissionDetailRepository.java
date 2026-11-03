package uz.uzkassa.cts.repository;

import org.springframework.stereotype.Repository;
import uz.uzkassa.cts.domain.ActOfTransmissionDetail;
import uz.uzkassa.cts.repository.custom.ActOfTransmissionDetailRepositoryCustom;

@Repository
public interface ActOfTransmissionDetailRepository extends BaseRepository<ActOfTransmissionDetail, Long>, ActOfTransmissionDetailRepositoryCustom {

    ActOfTransmissionDetail findByDeviceId(Long deviceId);

    ActOfTransmissionDetail findFirstByDeviceIdAndActOfTransmission_Contract_CustomerId(Long deviceId, Long customerId);
}
