package uz.uzkassa.smartpossupply.crons;

import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Profile;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import uz.uzkassa.smartpossupply.repository.TerminalLogRepository;

import static lombok.AccessLevel.PRIVATE;

/**
 * Created time: 04.06.2025 11:01
 */

@Component
@Slf4j
@FieldDefaults(level = PRIVATE, makeFinal = true)
@RequiredArgsConstructor
@Profile("prod")
public class TerminalCronJob {

    TerminalLogRepository terminalLogRepository;

    @Scheduled(cron = "0 0 1 */3 * *") // At 01:00, every 3 days
    public void deleteTerminalLog() {
        log.info("Delete Terminal Log cron job started");
        terminalLogRepository.deleteAll();
    }
}