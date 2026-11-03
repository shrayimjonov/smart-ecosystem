package uz.uzkassa.cts.repository;


import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import uz.uzkassa.cts.domain.APayPayment;
import uz.uzkassa.cts.enums.apay.APayPaymentStatus;

import java.util.List;
import java.util.Optional;

@Repository
public interface APayPaymentRepository extends BaseRepository<APayPayment, Long> {

    @Query("select distinct p from APayPayment p where p.aPayPaymentStatus = :status")
    List<APayPayment> findByAPayPaymentStatus(@Param("status") APayPaymentStatus status);

    Optional<APayPayment> findFirstByBillId(String billId);
}
