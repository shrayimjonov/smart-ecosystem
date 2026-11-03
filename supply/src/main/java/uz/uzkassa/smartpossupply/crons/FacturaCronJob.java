package uz.uzkassa.smartpossupply.crons;

import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Profile;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import uz.uzkassa.smartpossupply.service.FacturaService;

import static lombok.AccessLevel.PRIVATE;

@Component
@Slf4j
@Profile("prod")
@FieldDefaults(level = PRIVATE, makeFinal = true)
@RequiredArgsConstructor
public class FacturaCronJob {

    FacturaService facturaService;

    //Fire at every day at 09:01 am
    //@Scheduled(cron = "0 1 9 * * * ")
    public void syncVatRegistrationCodes() {
        facturaService.syncVatRegistrationCodes();
    }

    //Fire at every day at 09:30 am
    @Scheduled(cron = "0 30 9 * * * ")
    public void syncFacturaGUID() {
        facturaService.syncFacturaGUID();
    }

}
