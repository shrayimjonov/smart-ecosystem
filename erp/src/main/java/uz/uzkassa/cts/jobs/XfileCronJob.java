package uz.uzkassa.cts.jobs;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Profile;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import uz.uzkassa.cts.domain.*;
import uz.uzkassa.cts.dto.invoice.mappings.InvoiceToFacturaMapping;
import uz.uzkassa.cts.enums.ContractStatus;
import uz.uzkassa.cts.rabbitmq.producer.RabbitMqXfileProducer;
import uz.uzkassa.cts.repository.RentInvoiceRepository;
import uz.uzkassa.cts.service.*;

import java.time.LocalDate;
import java.time.Month;
import java.util.List;

@Component
@Slf4j
@Profile("prod")
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
@RequiredArgsConstructor
public class XfileCronJob {

    RentInvoiceRepository rentInvoiceRepository;
    RabbitMqXfileProducer xfileProducer;
    ServiceOrderInvoiceOldService serviceOrderInvoiceOldService;
    OneTimeOrderInvoiceOldService oneTimeOrderInvoiceOldService;
    ActOfTransmissionInvoiceOldService actOfTransmissionInvoiceOldService;
    TradeInInvoiceOldService tradeInInvoiceOldService;
    SaleInvoiceOldService saleInvoiceOldService;

    //@Scheduled(cron = "0 0 6 * * ?")
    @Scheduled(cron = "0 0 6,10,12,14,16,18 * * ?")
    @Profile("prod")
    public void serviceOrderInvoiceSend() {
        log.info(">>>>>>>>>> Start Cron Service Order Invoice Send <<<<<<<<<<<<");
        LocalDate now = LocalDate.now();
        LocalDate beginDate = LocalDate.of(2020, 11, 1);
        serviceOrderInvoiceSend(serviceOrderInvoiceOldService.getInvoicesForXfile(beginDate, now, ContractStatus.ACCEPTED));
        log.info(">>>>>>>>>> End Cron Service Order Invoice Send <<<<<<<<<<<<");
    }

    private void serviceOrderInvoiceSend(List<ServiceOrderInvoice> invoices) {
        invoices.stream().map(InvoiceToFacturaMapping::mapInvoiceToFactura).forEach(xfileProducer::sendServiceOrderInvoice);
    }


    //Fire at 02:01 AM every day
    @Scheduled(cron = "0 0 6 * * ?")
    @Profile("prod")
    public void oneTimeOrderInvoiceSend() {
        log.info(">>>>>>>>>> Start Cron One Time Order Invoice Send <<<<<<<<<<<<");
        LocalDate now = LocalDate.now();
        oneTimeOrderInvoiceSend(oneTimeOrderInvoiceOldService.getInvoicesForXfile(LocalDate.of(2023, Month.OCTOBER, 1), now, ContractStatus.ACCEPTED));
        log.info(">>>>>>>>>> End Cron One Time Order Invoice Send <<<<<<<<<<<<");
    }

    private void oneTimeOrderInvoiceSend(List<OneTimeOrderInvoice> invoices) {
        for (OneTimeOrderInvoice invoice : invoices) {
            xfileProducer.sendOneTimeOrderInvoice(InvoiceToFacturaMapping.mapInvoiceToFactura(invoice));
        }
    }

    //Fire at 02:01 AM every day
    @Scheduled(cron = "0 0 6 * * ?")
    @Profile("prod")
    public void actOfTransmissionInvoiceSend() {
        log.info(">>>>>>>>>> Start Cron Act Of Transmission Invoice Send <<<<<<<<<<<<");
        actOfTransmissionInvoiceOldService.findInvoicesToSendXfile().forEach(invoice -> xfileProducer.sendActOfTransmissionInvoice(InvoiceToFacturaMapping.mapInvoiceToFactura(invoice)));
        log.info(">>>>>>>>>> End Cron Act Of Transmission Invoice Send <<<<<<<<<<<<");
    }

    //Fire every 10 minutes
    @Scheduled(cron = "0 */10 * ? * *")
    @Profile("prod")
    public void tradeInInvoiceSend() {
        log.info(">>>>>>>>>> Start Cron Trade-in Invoice Send <<<<<<<<<<<<");
        tradeInInvoiceOldService.findInvoicesToSendXfile().forEach(invoice -> xfileProducer.sendTradeInInvoice(InvoiceToFacturaMapping.mapInvoiceToFactura(invoice)));
        log.info(">>>>>>>>>> End Cron Trade-in Invoice Send <<<<<<<<<<<<");
    }

    //Fire at 02:01 AM every day
    @Scheduled(cron = "0 0 7 * * ?")
    @Profile("prod")
    public void rentInvoiceSend() {
        log.info(">>>>>>>>>> Start Cron Act Of Transmission Invoice Send <<<<<<<<<<<<");
        rentInvoiceRepository.findByXfileIdIsNull().forEach(invoice -> xfileProducer.sendRentInvoice(InvoiceToFacturaMapping.mapInvoiceToFactura(invoice)));
        log.info(">>>>>>>>>> End Cron Act Of Transmission Invoice Send <<<<<<<<<<<<");
    }

    //Fire every 10 minutes
    @Scheduled(cron = "0 */10 * ? * *")
    @Profile("prod")
    public void saleInvoiceSend() {
        log.info(">>>>>>>>>> START CRON SALE INVOICE SEND <<<<<<<<<<<<");
        saleInvoiceOldService.findInvoicesToSendXfile().forEach(invoice -> xfileProducer.sendSaleInvoice(InvoiceToFacturaMapping.mapSaleInvoiceToFactura(invoice)));
        log.info(">>>>>>>>>> END CRON SALE INVOICE SEND <<<<<<<<<<<<");
    }
}
