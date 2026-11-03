package uz.uzkassa.smartpossupply.crons;

import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Profile;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import uz.uzkassa.smartpossupply.service.LgotaPrefixService;

import static lombok.AccessLevel.PRIVATE;

@Component
@Slf4j
@Profile("prod")
@FieldDefaults(level = PRIVATE, makeFinal = true)
@RequiredArgsConstructor
public class LgotaPrefixCronJob {

    LgotaPrefixService lgotaPrefixService;

    @Scheduled(cron = "0 0 6 * * MON")
    public void sync() {
        log.info(">>>>>>>>>> Start Cron Lgota Prefix Sync  <<<<<<<<<<<<");
        lgotaPrefixService.sync();
    }
}
