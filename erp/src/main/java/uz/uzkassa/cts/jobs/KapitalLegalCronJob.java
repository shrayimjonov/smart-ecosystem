package uz.uzkassa.cts.jobs;


import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Profile;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import uz.uzkassa.cts.service.KapitalLegalPaymentDetailService;
import uz.uzkassa.cts.service.KapitalLegalPaymentService;

@Component
@Slf4j
@Profile("prod")
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
@RequiredArgsConstructor
public class KapitalLegalCronJob {

    KapitalLegalPaymentService kapitalLegalPaymentService;
    KapitalLegalPaymentDetailService kapitalLegalPaymentDetailService;

    //Fire at every hour
    @Scheduled(cron = "0 0 * ? * *")
    @Profile("prod")
    public void syncWithKapitalLegal() {
        log.info(">>>>>>>>>> Start Cron Sync With Kapital Legal <<<<<<<<<<<<");
        kapitalLegalPaymentService.checkForPayments();
        log.info(">>>>>>>>>> End Cron Sync With Kapital Legal <<<<<<<<<<<<");
    }

    //Fire at every 1 hour 10 minutes
    @Scheduled(cron = "0 */30 * ? * *")
    @Profile("prod")
//    @Scheduled(fixedRate = 3600000, initialDelay = 600000)//1 hour, Run after 10 min
    private void autoSendToBilling() {
        log.info(">>>>>>>>>> Start Cron Auto Send To Billing <<<<<<<<<<<<");
        kapitalLegalPaymentDetailService.sentToBillingCronJob();
        log.info(">>>>>>>>>> End Cron Auto Send To Billing <<<<<<<<<<<<");
    }
}
