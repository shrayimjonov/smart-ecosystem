package uz.uzkassa.cts.service;

import uz.uzkassa.cts.domain.ActOfTransmission;
import uz.uzkassa.cts.domain.ActPaymentSchedule;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

public interface ActPaymentScheduleService extends BaseService<ActPaymentSchedule, Long> {
    List<ActPaymentSchedule> getScheduleForCron(LocalDate date);

    BigDecimal getCurrentMonthExpectation(LocalDate from, LocalDate to);

    void calculateBalanceByAct(ActOfTransmission act);

    void calculateBalanceByDate();

    void calculateBalanceById(Long id);
}
