package uz.uzkassa.cts.jobs;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Profile;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import uz.uzkassa.cts.service.BillService;

@Slf4j
@Component
@Profile("prod")
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
@RequiredArgsConstructor
public class BillCronJob {

    BillService billService;

    @Profile("prod")
    @Scheduled(cron = "0 0 5 * * ?")  // At 05:00 AM
    public void sync() {
        log.info("Start canceled over time BILL");
        billService.canceledOverTime();
        log.info("End canceled over time BILL");
    }
}
