package uz.uzkassa.cts.jobs;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import uz.uzkassa.cts.domain.KapitalbankPayment;
import uz.uzkassa.cts.enums.KapitalbankPaymentStatus;
import uz.uzkassa.cts.repository.KapitalbankPaymentRepository;
import uz.uzkassa.cts.service.KapitalBankService;

import java.util.List;

@Component
@Slf4j
@Profile("prod")
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
@RequiredArgsConstructor
public class KapitalbankCronJob {

    KapitalBankService kapitalBankService;
    KapitalbankPaymentRepository kapitalbankPaymentRepository;

    //@Scheduled(cron = "0 */5 * ? * *")
    @Profile("prod")
    public void checkPaymentStatus() {
        List<KapitalbankPayment> payments = kapitalbankPaymentRepository.findByKapitalbankPaymentStatusAndPayStatus(KapitalbankPaymentStatus.WAITING_FOR_APPROVAL, "OK");
        log.info("Check payment status begin payments count: {}", payments.size());
        log.info(">>>>>>>>>> Start Cron Check Payment Status <<<<<<<<<<<<");
        for (KapitalbankPayment payment : payments) {
            try {
                kapitalBankService.checkPaymentStatus(payment);
            } catch (Exception ignored) {
            }
        }
        log.info(">>>>>>>>>> End Cron Check Payment Status <<<<<<<<<<<<");
    }
}
