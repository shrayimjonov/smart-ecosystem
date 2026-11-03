package uz.uzkassa.cts.repository;

import org.springframework.stereotype.Repository;
import uz.uzkassa.cts.domain.PaymentScheduleInvoice;
import uz.uzkassa.cts.repository.custom.PaymentScheduleInvoiceRepositoryCustom;

import java.util.List;
import java.util.Optional;

@Repository
public interface PaymentScheduleInvoiceRepository extends BaseRepository<PaymentScheduleInvoice, Long>, PaymentScheduleInvoiceRepositoryCustom {

//    List<PaymentScheduleInvoice> findBySchedule_ActOfTransmission_IdOrderByIdDesc(Long actOfTransmissionId);

    List<PaymentScheduleInvoice> findBySchedule_ActOfTransmissionDetail_IdOrderByIdDesc(Long actOfTransmissionId);

    Optional<PaymentScheduleInvoice> findBySchedule_id(Long scheduleId);
}
