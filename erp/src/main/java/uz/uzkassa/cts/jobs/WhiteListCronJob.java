package uz.uzkassa.cts.jobs;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.context.annotation.Profile;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import uz.uzkassa.cts.service.WhiteListService;

@Component
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
@RequiredArgsConstructor
@Profile("prod")
public class WhiteListCronJob {

    WhiteListService whiteListService;

    @Scheduled(cron = "0 30 0 * * ?") // Fire every day at 00:30
    public void create() {
        whiteListService.updateStatusCron();
    }
}
