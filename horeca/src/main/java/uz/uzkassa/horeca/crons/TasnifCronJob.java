package uz.uzkassa.horeca.crons;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import uz.uzkassa.horeca.dto.payload.TasnifBasketProductPayload;
import uz.uzkassa.horeca.enums.CommonStatus;
import uz.uzkassa.horeca.rabbitmq.producer.RabbitMqTasnifProducer;
import uz.uzkassa.horeca.repository.CompanyRepository;

@Component
@Slf4j
@Profile("prod")
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
@RequiredArgsConstructor
public class TasnifCronJob {

    RabbitMqTasnifProducer rabbitMqTasnifProducer;
    CompanyRepository companyRepository;

    //Fire at every day at 01:00 AM
    //@Scheduled(cron = "0 0 1 * * * ")
    public void sync() {
        companyRepository
            .findAllByStatusAndOwnerIdIsNotNullAndDeletedIsFalse(CommonStatus.ACTIVE)
            .forEach(company -> rabbitMqTasnifProducer.sync(new TasnifBasketProductPayload(company.getId(), company.getTin())));
    }
}
