package uz.uzkassa.cts.repository;

import org.springframework.stereotype.Repository;
import uz.uzkassa.cts.domain.PaymentAct;
import uz.uzkassa.cts.enums.apay.APayStatus;

import java.util.List;
import java.util.Optional;

@Repository
public interface PaymentActRepository extends BaseRepository<PaymentAct, Long> {

    List<PaymentAct> findByContract_Id(Long contractId);

    Optional<PaymentAct> findFirstByDeviceIdAndContract_CustomerId(Long deviceId, Long customerId);

    List<PaymentAct> findAllByContract_CustomerIdAndPaymentStatus(Long customerId, APayStatus payStatus);
}
