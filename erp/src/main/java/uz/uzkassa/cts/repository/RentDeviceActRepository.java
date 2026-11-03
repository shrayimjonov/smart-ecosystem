package uz.uzkassa.cts.repository;

import org.springframework.stereotype.Repository;
import uz.uzkassa.cts.domain.RentDeviceAct;
import uz.uzkassa.cts.enums.ContractStatus;

import java.util.List;

@Repository
public interface RentDeviceActRepository extends BaseRepository<RentDeviceAct, Long> {

    List<RentDeviceAct> findByCustomerIdAndStatusIn(Long customerId, List<ContractStatus> statuses);
}
