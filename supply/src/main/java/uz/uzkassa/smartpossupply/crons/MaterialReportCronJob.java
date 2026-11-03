package uz.uzkassa.smartpossupply.crons;

import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Profile;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import uz.uzkassa.smartpossupply.service.MaterialReportService;

import static lombok.AccessLevel.PRIVATE;

/**
 * Created time: 08.05.2025 16:45
 */

@Component
@Slf4j
@FieldDefaults(level = PRIVATE, makeFinal = true)
@RequiredArgsConstructor
@Profile("prod")
public class MaterialReportCronJob {

    MaterialReportService materialReportService;

    @Scheduled(cron = "0 5 0 ? * *") // every day at 00:05
    public void create() {
        log.info("Start Material Report Cron Job");
        materialReportService.sync();
        log.info("End Material Report Cron Job");
    }

    //Fire every day at midnight - 2am
    @Scheduled(cron = "0 0 2 * * ?")
    public void resync() {
        log.info("Start Material Report Resync Cron Job");
        materialReportService.resync();
        log.info("End Material Report Resync Cron Job");
    }
}
