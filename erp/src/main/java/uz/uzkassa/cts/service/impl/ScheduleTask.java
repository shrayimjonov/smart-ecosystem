package uz.uzkassa.cts.service.impl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import uz.uzkassa.cts.service.CronInvoiceService;
import uz.uzkassa.cts.service.DeviceTariffService;

import java.time.LocalDate;

// "0 0 * * * *" = the top of every hour of every day.
// "*/10 * * * * *" = every ten seconds.
// "0 0 8-10 * * *" = 8, 9 and 10 o'clock of every day.
// "0 0 8,10 * * *" = 8 and 10 o'clock of every day.
// "0 0/30 8-10 * * *" = 8:00, 8:30, 9:00, 9:30 and 10 o'clock every day.
// "0 0 9-17 * * MON-FRI" = on the hour nine-to-five weekdays
//
@Component
@Slf4j
public class ScheduleTask {

    private final CronInvoiceService cronInvoiceService;
    private final DeviceTariffService deviceTariffService;

    public ScheduleTask(CronInvoiceService cronInvoiceService, DeviceTariffService deviceTariffService) {
        this.cronInvoiceService = cronInvoiceService;
        this.deviceTariffService = deviceTariffService;
    }


    public void generateInvoice() {
        log.info("cron is begin at {} ", LocalDate.now());
//        if (LocalDate.now().equals(LocalDateUtil.getMonthLastDate()))
        cronInvoiceService.generateInvoice(null);
        log.info("Cron finish task at {}", LocalDate.now());
    }

    //    @Scheduled(cron = "0 0 0/1 * * ?")
//    @Scheduled(fixedRate = 60 * 60 * 1000)
    @Profile("prod")
    private void blockDevice() {
        log.info("cron is begin at {} ", LocalDate.now());
        deviceTariffService.switchDevices(null);
        log.info("Cron finish task at {}", LocalDate.now());
    }

    private void sendBlockInfoToLauncher() {
        log.info("cron is begin");
        //launcherService.action();
        log.info("Cron finish task");
    }


}
