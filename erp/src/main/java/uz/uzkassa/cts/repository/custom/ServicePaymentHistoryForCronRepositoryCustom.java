package uz.uzkassa.cts.repository.custom;

import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ServicePaymentHistoryForCronRepositoryCustom {

    List<Object[]> getCustomerDevices(String tin);
}
