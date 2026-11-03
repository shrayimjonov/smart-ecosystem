package uz.uzkassa.horeca.crons;

import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Profile;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import uz.uzkassa.horeca.service.ReservationService;

import java.time.LocalDateTime;

/**
 * Powered by: Shuxratjon Rayimjonov
 * Date: 28.05.2024
 */
@Component
@Slf4j
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
//@Profile("prod")
public class ReservationCronJob {

    ReservationService reservationService;

    public ReservationCronJob(ReservationService reservationService) {
        this.reservationService = reservationService;
    }

    //Fire in every 10 minutes in every day from 10AM to 10PM
    @Scheduled(cron = "0 0/10 10-22 * * ?")
    public void activate() {
        log.info("==========================================Cron job started==========================================");
        log.info("==========================================Time: {}==========================================", LocalDateTime.now());
        reservationService.activateReservation();
    }

    //Fire in every day at 00:01
    @Scheduled(cron = "0 1 0 * * ?")
    public void remove() {
        log.info("==========================================Cron job started==========================================");
        log.info("==========================================Time: {}==========================================", LocalDateTime.now());
        reservationService.removeReservations();
    }
}