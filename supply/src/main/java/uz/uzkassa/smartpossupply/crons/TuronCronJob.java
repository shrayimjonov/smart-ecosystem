package uz.uzkassa.smartpossupply.crons;

import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import uz.uzkassa.smartpossupply.enums.BufferStatus;
import uz.uzkassa.smartpossupply.service.MarkOrderProductService;

import static lombok.AccessLevel.PRIVATE;

@Component
@Slf4j
@Profile("prod")
@FieldDefaults(level = PRIVATE, makeFinal = true)
@RequiredArgsConstructor
public class TuronCronJob {

    MarkOrderProductService markOrderProductService;

    /**
     * Check status of created orders from Turon.
     * This is scheduled to get fired every 1 minute.
     */
//    @Scheduled(fixedDelay = 60000)
    public void checkStatus() {
        markOrderProductService.checkStatus(BufferStatus.PENDING);
    }

    /**
     * Update status of active orders from Turon.
     * This is scheduled to get fired every 1 hour.
     */
//    @Scheduled(fixedDelay = 3600000)
    public void updateStatus() {
        markOrderProductService.checkStatus(BufferStatus.ACTIVE);
    }
}
