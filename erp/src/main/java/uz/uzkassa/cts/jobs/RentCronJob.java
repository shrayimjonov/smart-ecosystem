package uz.uzkassa.cts.jobs;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Profile;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import uz.uzkassa.cts.service.RentInvoiceService;
import uz.uzkassa.cts.service.RentPaymentHistoryService;

@Component
@Slf4j
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
@RequiredArgsConstructor
@Profile("prod")
public class RentCronJob {

    RentInvoiceService rentInvoiceService;
    RentPaymentHistoryService rentPaymentHistoryService;

    @Scheduled(cron = "0 0 2 * * ?")
    public void createRentInvoice() {
        rentInvoiceService.createInvoiceCron();
    }

    @Scheduled(cron = "0 0 1 * * ?")
    public void createPaymentHistory() {
        rentPaymentHistoryService.createPaymentHistoryCron();
    }

    @Scheduled(cron = "0 0 0 6 * ?")
    public void calculateBonus() {
        rentPaymentHistoryService.calculateBonusCron();
    }
}
