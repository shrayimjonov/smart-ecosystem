package uz.uzkassa.smartpossupply.crons;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.context.annotation.Profile;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import uz.uzkassa.smartpossupply.service.SubscriptionNotificationService;

@Component
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor
@Profile("prod")
public class SubscriptionNotificationCronJob {

    SubscriptionNotificationService subscriptionNotificationService;

    @Scheduled(cron = "0 0 10 * * *") // every hour
    public void processSubscriptionNotifications() {
        subscriptionNotificationService.process();
    }
}
