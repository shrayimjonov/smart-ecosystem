package uz.uzkassa.cts.repository;

import org.springframework.stereotype.Repository;
import uz.uzkassa.cts.domain.CustomerBranchDevicePaymentType;
import uz.uzkassa.cts.enums.PaymentType;

import java.util.List;

@Repository
public interface CustomerBranchDevicePaymentTypeRepository extends BaseRepository<CustomerBranchDevicePaymentType, Long> {
    void deleteAllByCustomerBranchDeviceId(Long customerBranchDeviceId);

    List<CustomerBranchDevicePaymentType> findByCustomerBranchDeviceIdInAndPaymentType(List<Long> customerBranchDeviceIds, PaymentType paymentType);
}
