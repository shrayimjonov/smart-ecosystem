package uz.uzkassa.cts.jobs;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Profile;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import uz.uzkassa.cts.service.APayPaymentService;

@Component
@Slf4j
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
@RequiredArgsConstructor
@Profile("prod")
public class APayPaymentCronJob {

    APayPaymentService aPayPaymentService;

    @Profile("prod")
    @Scheduled(cron = "0 */5 * ? * *")
    public void checkPaymentStatus() {
        aPayPaymentService.checkPaymentStatusCron();
    }
}
