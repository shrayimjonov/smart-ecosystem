package uz.uzkassa.smartpossupply.crons;

import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Profile;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import uz.uzkassa.smartpossupply.dto.payload.LgotaSyncPayload;
import uz.uzkassa.smartpossupply.enums.Vat;
import uz.uzkassa.smartpossupply.rabbitmq.producer.RabbitMqLgotaSyncProducer;
import uz.uzkassa.smartpossupply.repository.ProductRepository;

import static lombok.AccessLevel.PRIVATE;

@Component
@Slf4j
@Profile("prod")
@FieldDefaults(level = PRIVATE, makeFinal = true)
@RequiredArgsConstructor
public class LgotaCronJob {

    RabbitMqLgotaSyncProducer rabbitMqLgotaSyncProducer;
    ProductRepository productRepository;

    //Fire at every day at 03:30 am
    @Scheduled(cron = "0 30 3 * * * ")
    public void sync() {
        log.info(">>>>>>>>>> Start Cron Lgota Sync  <<<<<<<<<<<<");
        productRepository.findAllByVat(Vat.WITHOUT_VAT.getCode()).
                forEach(catalogCode -> rabbitMqLgotaSyncProducer.sendToQueue(new LgotaSyncPayload(catalogCode)));
    }
}
