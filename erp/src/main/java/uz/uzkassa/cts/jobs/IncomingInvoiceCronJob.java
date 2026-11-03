package uz.uzkassa.cts.jobs;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import uz.uzkassa.cts.config.ApplicationProperties;
import uz.uzkassa.cts.dto.factura.GetIncomingInvoiceRequest;
import uz.uzkassa.cts.service.XfileService;
import uz.uzkassa.cts.service.IncomingInvoiceService;

import java.time.LocalDateTime;
import java.time.LocalTime;

@Component
@Slf4j
@Profile("prod")
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
@RequiredArgsConstructor
public class IncomingInvoiceCronJob {

    XfileService xfileService;
    ApplicationProperties applicationProperties;
    IncomingInvoiceService incomingInvoiceService;

    //Fire at every 1 hour after 15 minutes
//    @Scheduled(fixedRate = 3600000, initialDelay = 900000)//1 hours. Run after 15 min
    // changed to every 5 minute
    //@Scheduled(cron = "0 */5 * ? * *")
    @Profile("prod")
    public void syncWithXFile() {
        log.info(">>>>>>>>>> Start Cron Sync With XFile <<<<<<<<<<<<");
        GetIncomingInvoiceRequest request = new GetIncomingInvoiceRequest();
        request.setPage(1);
        request.setSize(1);
        request.setSupplierTin(applicationProperties.getXfileProperty().getPrimeTin());
        request.setStatus("ACCEPTED");

        LocalDateTime fromDate = LocalDateTime.now().minusDays(1).toLocalDate().atStartOfDay();
        LocalDateTime toDate = LocalDateTime.now().toLocalDate().atTime(LocalTime.MAX);

        request.setFrom(fromDate);
        request.setTo(toDate);

        Long count = xfileService.getTotalDataByTin(request);
        if (count > 0) {
            int start = 0;
            int limit = 1000;
            request.setSize(limit);
            while (start <= count) {
                request.setPage((start / limit) + 1);
                incomingInvoiceService.saveInvoices(request);
                start += limit;
            }
        }
        log.info(">>>>>>>>>> End Cron Sync With XFile <<<<<<<<<<<<");
    }

}
