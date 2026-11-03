package uz.uzkassa.cts.jobs;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Profile;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import uz.uzkassa.cts.service.OneTimeOrderInvoiceOldService;

@Component
@Slf4j
@Profile("prod")
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
@RequiredArgsConstructor
public class OneTimeOrderInvoiceCronJob {

    OneTimeOrderInvoiceOldService oneTimeOrderInvoiceOldService;

    @Scheduled(cron = "0 */10 * ? * *")
    @Profile("prod")
    public void createOneTimeInvoice() {
        //From 28.08.20 One time invoices
        // must create immediately after approve of order
        // Before was created on end of every month
//        if (DateUtil.getMonthLastDate().isEqual(LocalDate.now())) {
        log.info(">>>>>>>>>> Start Cron Create One Time Invoice <<<<<<<<<<<<");
        oneTimeOrderInvoiceOldService.createInvoice();
        log.info(">>>>>>>>>> End Cron Create One Time Invoice <<<<<<<<<<<<");
//        }
    }
}
