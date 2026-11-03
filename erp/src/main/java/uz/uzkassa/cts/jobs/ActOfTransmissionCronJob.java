package uz.uzkassa.cts.jobs;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Profile;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import uz.uzkassa.cts.domain.ActOfTransmission;
import uz.uzkassa.cts.domain.ActPaymentSchedule;
import uz.uzkassa.cts.service.ActOfTransmissionFactoryService;
import uz.uzkassa.cts.service.ActOfTransmissionInvoiceOldService;
import uz.uzkassa.cts.service.ActPaymentScheduleService;
import uz.uzkassa.cts.service.PaymentScheduleInvoiceService;

import java.time.LocalDate;
import java.util.List;

@Component
@Slf4j
@Profile("prod")
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
@RequiredArgsConstructor
public class ActOfTransmissionCronJob {

    ActPaymentScheduleService service;
    ActOfTransmissionFactoryService transmissionService;
    PaymentScheduleInvoiceService paymentScheduleInvoiceService;
    ActOfTransmissionInvoiceOldService actOfTransmissionInvoiceOldService;

    @Scheduled(cron = "0 */30 * ? * *")
    @Profile("prod")
    public void createInvoiceForBalanceChange() {
        List<ActPaymentSchedule> schedules = service.getScheduleForCron(LocalDate.now());
        log.info("ActPaymentSchedule cron run ------ schedules size: {}", schedules.size());
        for (ActPaymentSchedule schedule : schedules) {
            try {
                paymentScheduleInvoiceService.createInvoiceAndChangeBalance(schedule);
            } catch (Exception ex) {
                log.error("ActPaymentSchedule cron exception: {}", ex.getMessage());
            }
        }
    }

    //Start at 04:01 am every day
//    @Scheduled(cron = "0 0 4 * * ?")
//    @Profile("prod")
    public void calculateBalance() {
        log.info("Calculate customer balance cron run");
        Thread.startVirtualThread(service::calculateBalanceByDate);
    }

    public void createInvoiceForBalanceChange(LocalDate date) {
        List<ActPaymentSchedule> schedules = service.getScheduleForCron(date);
        log.info("ActPaymentSchedule cron run ------ schedules size: {}", schedules.size());
        for (ActPaymentSchedule schedule : schedules) {
            try {
                paymentScheduleInvoiceService.createInvoiceAndChangeBalance(schedule);
            } catch (Exception ex) {
                log.error("ActPaymentSchedule cron exception: {}", ex.getMessage());
            }
        }
    }

    @Scheduled(cron = "0 */30 * ? * *")
    @Profile("prod")
    public void changeActStatus() {
        log.info("change act stats cron run ------ -----");
        try {
            paymentScheduleInvoiceService.changeActStatus();
        } catch (Exception ex) {
            log.error("change act stats cron exception: {}", ex.getMessage());
        }
    }

    @Scheduled(cron = "0 */30 * ? * *")
    @Profile("prod")
    public void createInvoiceForXfile() {
        List<ActOfTransmission> acts = transmissionService.findForInvoiceCreate();
        log.info("INVOICE for xfile cron run ------ acts size: {}", acts.size());
        for (ActOfTransmission act : acts) {
            try {
                actOfTransmissionInvoiceOldService.createInvoiceForCron(act);
            } catch (Exception ex) {
                log.error("ActOfTransmissionInvoice cron exception: {}", ex.getMessage());
            }
        }
    }
}
