package uz.uzkassa.cts.repository;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import uz.uzkassa.cts.domain.ActPaymentSchedule;
import uz.uzkassa.cts.enums.CompanyType;
import uz.uzkassa.cts.enums.apay.APayStatus;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Repository
public interface ActPaymentScheduleRepository extends BaseRepository<ActPaymentSchedule, Long> {

    @Query("select distinct act from ActPaymentSchedule act " +
            "inner join act.actOfTransmissionDetail ad " +
            "inner join ad.actOfTransmission ac " +
            "where act.deleted = false " +
            " and act.paymentDate = :date " +
            " and (act.status != 'REJECTED' or act.status is null) and (act.invoiceId is null) and ac.companyType =:companyType ")
    List<ActPaymentSchedule> findByPaymentDateAndStatusNotAndInvoiceIdIsNull(@Param("date") LocalDate date,
                                                                             @Param("companyType") CompanyType companyType);

    @Query("select sum(s.amount) from ActPaymentSchedule s where s.invoiceId is null and s.paymentDate between :from and :to")
    BigDecimal getCurrentMonthExpectation(@Param("from") LocalDate from, @Param("to") LocalDate to);

    @Query(value = "select sc from ActPaymentSchedule sc \n" +
            "inner join sc.actOfTransmissionDetail det \n" +
            "inner join det.actOfTransmission act\n " +
            "inner join act.contract con \n " +
            "where con.status = 'ACCEPTED' and act.status = 'ACCEPTED' " +
            "and (sc.status <> 'ACCEPTED' or sc.status is null) \n" +
            "and sc.paymentDate = :date and sc.paymentStatus = :paymentStatus \n order by sc.id ")
    List<ActPaymentSchedule> findAllByDateAndContractId(@Param("date") LocalDate date, @Param("paymentStatus") APayStatus paymentStatus);

    @Query(value = "select sc from ActPaymentSchedule sc \n" +
            "inner join sc.actOfTransmissionDetail det \n" +
            "inner join det.actOfTransmission act\n " +
            "inner join act.contract con \n " +
            "where con.status = 'ACCEPTED' and act.status = 'ACCEPTED' " +
            "and (sc.status <> 'ACCEPTED' or sc.status is null) \n" +
            "and sc.paymentStatus = :paymentStatus and con.id = :contractId \n order by sc.id limit 1 ")
    ActPaymentSchedule findAllByDateAndContractIdAndStatus(@Param("paymentStatus") APayStatus paymentStatus, @Param("contractId") Long contractId);
}
