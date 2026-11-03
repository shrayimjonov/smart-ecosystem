package uz.uzkassa.cts.repository;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import uz.uzkassa.cts.domain.ServicePaymentHistory;
import uz.uzkassa.cts.repository.custom.ServicePaymentHistoryForCronRepositoryCustom;

import java.math.BigDecimal;
import java.time.Month;
import java.util.List;
import java.util.Set;

@Repository
public interface ServicePaymentHistoryForCronRepository extends BaseRepository<ServicePaymentHistory, Long>, ServicePaymentHistoryForCronRepositoryCustom {

    @Query(value = " select sph.* from service_payment_history sph " +
        " inner join (select sph.customer_branch_device_id, max(sph.id) as id from service_payment_history sph" +
        " where customer_id = :customerId and sph.customer_branch_device_id in (:customerBranchDeviceIds)" +
        " group by sph.customer_branch_device_id) det" +
        " on sph.id = det.id", nativeQuery = true)
    Set<ServicePaymentHistory> findLastHistories(
        @Param("customerId") Long customerId,
        @Param("customerBranchDeviceIds") List<Long> customerBranchDeviceIds);

    @Query("select distinct sph from ServicePaymentHistory sph " +
        "inner join sph.customer c " +
        "where c.tin = ?1 and sph.serviceOrderInvoiceId is null" +
        " and sph.deviceSerialNumber = ?2 and sph.month = ?3 ")
    ServicePaymentHistory getHistoryByDeviceSerialNumberAndTin(String tin, String deviceSerialNumber, Month month);


    @Query("select distinct sph from ServicePaymentHistory sph " +
        "inner join sph.customer c " +
        "inner join sph.customerBranchDevice cbd " +
        "inner join sph.tariff t " +
        "inner join cbd.device " +
        "where c.tin = ?1 and sph.serviceOrderInvoiceId is null and t.periodType = 'YEARLY' ")
    List<ServicePaymentHistory> findForInvoiceYearlyByCustomerTin(String tin);

    @Query("select distinct sph from ServicePaymentHistory sph " +
        "inner join sph.customer c " +
        "inner join sph.customerBranchDevice cbd " +
        "inner join sph.tariff t " +
        "inner join cbd.device " +
        "where c.tin = ?1 and sph.serviceOrderInvoiceId is null and t.periodType <> 'YEARLY' ")
    List<ServicePaymentHistory> findForInvoiceMonthlyByCustomerTin(String tin);

    @Query("select distinct sph from ServicePaymentHistory sph " +
        "inner join fetch sph.customerBranchDevice cbd " +
        "inner join sph.customer c " +
        "inner join fetch cbd.device d " +
        "where (c.tin = ?1 or c.oldTin = ?1) and sph.paid = false " +
        "order by sph.customerBranchDeviceId asc ")
    List<ServicePaymentHistory> findAllUnPaidCustomerTin(String tin);

    @Query("select distinct sum(sph.amount) from ServicePaymentHistory sph " +
        "inner join sph.customerBranchDevice cbd " +
        "inner join sph.customer c " +
        "inner join cbd.device d " +
        "where c.id = ?1 and sph.serviceOrderInvoiceId is null ")
    BigDecimal unpaidHistoriesSum(Long customerId);
}
