package uz.uzkassa.cts.service.impl;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uz.uzkassa.cts.domain.ActOfTransmission;
import uz.uzkassa.cts.domain.ActPaymentSchedule;
import uz.uzkassa.cts.domain.ContractForLeasing;
import uz.uzkassa.cts.domain.Customer;
import uz.uzkassa.cts.enums.CompanyType;
import uz.uzkassa.cts.enums.apay.APayStatus;
import uz.uzkassa.cts.repository.ActPaymentScheduleRepository;
import uz.uzkassa.cts.service.ActPaymentScheduleService;
import uz.uzkassa.cts.service.BillingTransactionService;
import uz.uzkassa.cts.service.PaymentActService;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Slf4j
@Service
public class ActPaymentScheduleServiceImpl extends BaseServiceImpl<ActPaymentSchedule, Long> implements ActPaymentScheduleService {

    private final ActPaymentScheduleRepository paymentScheduleRepository;
    private final BillingTransactionService billingTransactionService;
    private final PaymentActService paymentActService;

    public ActPaymentScheduleServiceImpl(ActPaymentScheduleRepository repository,
                                         BillingTransactionService billingTransactionService,
                                         PaymentActService paymentActService) {
        super(repository);
        this.paymentScheduleRepository = repository;
        this.billingTransactionService = billingTransactionService;
        this.paymentActService = paymentActService;
    }

    @Override
    public List<ActPaymentSchedule> getScheduleForCron(LocalDate date) {
        return paymentScheduleRepository.findByPaymentDateAndStatusNotAndInvoiceIdIsNull(date, CompanyType.STS);
    }

    @Override
    public BigDecimal getCurrentMonthExpectation(LocalDate from, LocalDate to) {
        return paymentScheduleRepository.getCurrentMonthExpectation(from, to);
    }

    @Override
    @Transactional
    public void calculateBalanceByAct(ActOfTransmission act) {
        ContractForLeasing contractForLeasing = act.getContract();
        /*The first priority 100% payment type is required */
        paymentActService.calculateCustomerBalance(contractForLeasing.getCustomerId());
        ActPaymentSchedule actPaymentSchedule = paymentScheduleRepository.findAllByDateAndContractIdAndStatus(APayStatus.PENDING, contractForLeasing.getId());
        if (actPaymentSchedule != null) {
            boolean isBalance = billingTransactionService.calculateBalance(actPaymentSchedule.getAmount(), contractForLeasing.getCustomerId());
            if (isBalance) {
                actPaymentSchedule.setPaymentStatus(APayStatus.PAID);
                paymentScheduleRepository.saveAndFlush(actPaymentSchedule);
            }
        }
    }

    @Override
    @Transactional
    public void calculateBalanceById(Long id) {
        ActPaymentSchedule schedule = findById(id);
        Customer customer = schedule.getActOfTransmissionDetail().getActOfTransmission().getContract().getCustomer();
        /*The first priority 100% payment type is required */
        paymentActService.calculateCustomerBalance(customer.getId());
        boolean isBalance = billingTransactionService.calculateBalance(schedule.getAmount(), customer.getId());
        if (isBalance) {
            schedule.setPaymentStatus(APayStatus.PAID);
            paymentScheduleRepository.saveAndFlush(schedule);
        }
    }

    @Override
    @Transactional
    public void calculateBalanceByDate() {
        List<ActPaymentSchedule> actPaymentScheduleList = paymentScheduleRepository.findAllByDateAndContractId(LocalDate.now(), APayStatus.PENDING);
        if (CollectionUtils.isEmpty(actPaymentScheduleList)) return;
        actPaymentScheduleList.forEach(schedule -> {
            boolean isBalance = billingTransactionService.calculateBalance(schedule.getAmount(), schedule.getActOfTransmissionDetail().getActOfTransmission().getContract().getCustomerId());
            if (isBalance) {
                schedule.setPaymentStatus(APayStatus.PAID);
                paymentScheduleRepository.saveAndFlush(schedule);
            }
        });
    }
}
