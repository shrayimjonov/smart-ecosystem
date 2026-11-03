package uz.uzkassa.cts.jobs;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Profile;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import uz.uzkassa.cts.service.BankTariffHistoryService;

@Slf4j
@Component
@Profile("prod")
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
@RequiredArgsConstructor
public class BankTariffHistoryCronJob {

    BankTariffHistoryService bankTariffHistoryService;

    @Profile("prod")
    @Scheduled(cron = "0 */10 * ? * *")  // Every 10 minutes
    public void sync() {
        log.info("Start bank tariff history.");
        bankTariffHistoryService.syncWithSmartPos();
        log.info("End bank tariff history");
    }
}
