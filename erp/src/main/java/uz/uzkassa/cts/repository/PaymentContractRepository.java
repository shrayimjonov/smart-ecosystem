package uz.uzkassa.cts.repository;

import org.springframework.stereotype.Repository;
import uz.uzkassa.cts.domain.PaymentContract;
import uz.uzkassa.cts.enums.ContractStatus;
import uz.uzkassa.cts.enums.DeviceType;

import java.util.List;

@Repository
public interface PaymentContractRepository extends BaseRepository<PaymentContract, Long> {

    List<PaymentContract> findByCustomerId(Long customerId);

    List<PaymentContract> findByCustomerIdAndStatusAndDeviceType(Long customerId, ContractStatus status, DeviceType deviceType);

    boolean existsByIndividualContractNumber(String contractNumber);
}
