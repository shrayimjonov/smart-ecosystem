package uz.uzkassa.cts.jobs;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.context.annotation.Profile;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import uz.uzkassa.cts.rabbitmq.producer.RabbitMqDeviceSwitcherProducer;
import uz.uzkassa.cts.service.CustomerOldService;

import java.util.List;

@Component
@Slf4j
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
@RequiredArgsConstructor
//@Profile("prod")
public class PaymentHistoryCreatorCronJob {

    RabbitMqDeviceSwitcherProducer rabbitMqProducer;
    CustomerOldService customerOldService;

    //Fire at 01:01 AM every day
    @Scheduled(cron = "0 0 1 * * ?")
    @Profile("prod")
    public void createHistory() {
        log.info(">>>>>>>>>> Start Cron Payment History Create <<<<<<<<<<<<");
        long count = customerOldService.count();
        int start = 0;
        int limit = 20;
        while (start <= count) {
            List<String> tins = customerOldService.getTinListInterval(start, limit);
            if (CollectionUtils.isNotEmpty(tins)) {
                tins.forEach(rabbitMqProducer::paymentHistoryCreate);
            }
            start += limit;
        }
        log.info(">>>>>>>>>> End Cron Payment History Create <<<<<<<<<<<<");
    }

    //Fire at 02:01 AM every day
    @Scheduled(cron = "0 0 2 * * ?")
    @Profile("prod")
    public void createServiceInvoice() {
        log.info(">>>>>>>>>> Start Cron Create Service Invoice <<<<<<<<<<<<");
        long count = customerOldService.count();
        int start = 0;
        int limit = 20;
        while (start <= count) {
            List<String> tins = customerOldService.getTinListInterval(start, limit);
            if (CollectionUtils.isNotEmpty(tins)) {
                tins.forEach(rabbitMqProducer::createServiceInvoice);
            }
            start += limit;
        }
        log.info(">>>>>>>>>> End Cron Create Service Invoice <<<<<<<<<<<<");
    }

    //Fire at 04:01 AM every day
    @Scheduled(cron = "0 0 4 * * ?")
    @Profile("prod")
    public void switchDevice() {
        log.info(">>>>>>>>>> Start Cron Switch Device <<<<<<<<<<<<");
        long count = customerOldService.count();
        int start = 0;
        int limit = 20;
        while (start <= count) {
            List<String> tins = customerOldService.getTinListInterval(start, limit);
            if (CollectionUtils.isNotEmpty(tins)) {
                tins.forEach(rabbitMqProducer::deviceStatusSwitch);
            }
            start += limit;
        }
        log.info(">>>>>>>>>> End Cron Switch Device <<<<<<<<<<<<");
    }

}
