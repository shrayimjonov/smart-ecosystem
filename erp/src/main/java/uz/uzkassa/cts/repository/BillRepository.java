package uz.uzkassa.cts.repository;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import uz.uzkassa.cts.domain.Bill;
import uz.uzkassa.cts.enums.apay.APayStatus;
import uz.uzkassa.cts.repository.custom.BillRepositoryCustom;

import java.time.LocalDateTime;
import java.util.Optional;

@Repository
public interface BillRepository extends BaseRepository<Bill, Long>, BillRepositoryCustom {

    Optional<Bill> findFirstByBillId(String billId);

    long countByPaymentQuoteId(Long paymentQuoteId);

    Optional<Bill> findFirstByDeviceId(Long deviceId);

    Optional<Bill> findByCustomerIdAndDeviceId(Long customerId, Long deviceId);

    @Modifying
    @Query("update Bill b set b.payStatus=:newStatus where b.payStatus=:oldStatus and b.createdDate < :fromDate ")
    void updateApayStatus(@Param("oldStatus") APayStatus oldStatus, @Param("newStatus") APayStatus newStatus, @Param("fromDate") LocalDateTime fromDate);
}
