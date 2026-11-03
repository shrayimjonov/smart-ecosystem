package uz.uzkassa.cts.repository;

import org.springframework.stereotype.Repository;
import uz.uzkassa.cts.domain.ServicePaymentHistory;

import java.time.LocalDate;
import java.time.Month;
import java.util.List;

@Repository
public interface ServicePaymentHistoryRepository extends BaseRepository<ServicePaymentHistory, Long> {

    List<ServicePaymentHistory> findByServiceOrderInvoiceId(Long serviceOrderInvoiceId);

    List<ServicePaymentHistory> findByPaidIsFalseAndCustomerIdOrderByCreatedDateDesc(Long customerId);

    ServicePaymentHistory findFirstByCustomerBranchDeviceIdAndMonthAndYear(Long customerBranchDeviceId, Month month, Integer year);
    ServicePaymentHistory findFirstByCustomerBranchDeviceIdAndStartDateAndDueDate(Long customerBranchDeviceId, LocalDate startDate, LocalDate endDate);
}
