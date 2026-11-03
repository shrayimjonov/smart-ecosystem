package uz.uzkassa.smartpossupply.crons;

import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Profile;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import uz.uzkassa.smartpossupply.enums.CommonStatus;
import uz.uzkassa.smartpossupply.repository.PriceListRepository;

import java.time.LocalDate;

import static lombok.AccessLevel.PRIVATE;

@Component
@Slf4j
@Profile("prod")
@FieldDefaults(level = PRIVATE, makeFinal = true)
@RequiredArgsConstructor
public class PriceListCronJob {

    PriceListRepository priceListRepository;

    //Fire every day at 4am
    @Scheduled(cron = "0 0 4 * * ?")
    public void checkExpireDate() {
        log.info(">>>>>>>>>> Start Cron Price list check expire date  <<<<<<<<<<<<");
        priceListRepository.updateStatus(CommonStatus.ACTIVE, CommonStatus.IN_ACTIVE, LocalDate.now());
        log.info(">>>>>>>>>> End Cron Price list check expire date  <<<<<<<<<<<<");
    }
}
