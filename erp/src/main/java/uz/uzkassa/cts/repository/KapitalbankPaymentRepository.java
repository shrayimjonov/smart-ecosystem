package uz.uzkassa.cts.repository;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import uz.uzkassa.cts.domain.KapitalbankPayment;
import uz.uzkassa.cts.enums.KapitalbankPaymentStatus;
import uz.uzkassa.cts.repository.custom.KapitalbankPaymentRepositoryCustom;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface KapitalbankPaymentRepository extends BaseRepository<KapitalbankPayment, Long>, KapitalbankPaymentRepositoryCustom {
    Optional<KapitalbankPayment> findByTransactionId(Integer transactionId);

    List<KapitalbankPayment> findByKapitalbankPaymentStatusAndPayStatus(KapitalbankPaymentStatus status, String payStatus);

    @Query("select distinct kbp from KapitalbankPayment kbp " +
            "where kbp.pan like CONCAT(:pan,'%') and kbp.bankClientId is null " +
            "and kbp.kapitalbankPaymentStatus = 2 " +
            "and kbp.createdDate BETWEEN :startDate AND :endDate " +
            " order by kbp.id, kbp.amount ASC ")
    List<KapitalbankPayment> getPaymentsForDetailing(@Param("pan") String pan,
                                                     @Param("startDate") LocalDateTime startDate,
                                                     @Param("endDate") LocalDateTime endDate);

    @Query("select max(payment.bankClientId) from KapitalbankPayment payment")
    Long getMaxBankClientId();
}
