package uz.uzkassa.smartpossupply.crons;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.context.annotation.Profile;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import uz.uzkassa.smartpossupply.service.TripService;

@Component
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor
@Profile("prod")
public class TripCronJob {
    TripService tripService;

    @Scheduled(cron = "0 0 4 * * *")
    public void execute() {
        tripService.create();
    }
}
