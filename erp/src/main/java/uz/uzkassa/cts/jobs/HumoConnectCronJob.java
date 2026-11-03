package uz.uzkassa.cts.jobs;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Profile;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import uz.uzkassa.cts.dto.humoconnect.request.HumoConnectLastReceiptRequestDTO;
import uz.uzkassa.cts.enums.CaseType;
import uz.uzkassa.cts.rabbitmq.producer.RabbitMqHumoConnectProducer;
import uz.uzkassa.cts.repository.HumoConnectRepository;

/**
 * Created date time: 18.02.2025 17:10
 */

@Component
@Slf4j
@Profile("prod")
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
@RequiredArgsConstructor
public class HumoConnectCronJob {

    HumoConnectRepository humoConnectRepository;
    RabbitMqHumoConnectProducer rabbitMqHumoConnectProducer;

    @Scheduled(cron = "0 0 2 * * ?") // every day at 02:00
    public void updateLastReceipt() {
        humoConnectRepository.findAllByCaseType(CaseType.HUMO_CONNECTION_CHANGE).forEach(humoConnect -> {
            HumoConnectLastReceiptRequestDTO humoConnectLastReceiptRequestDTO = new HumoConnectLastReceiptRequestDTO();
            humoConnectLastReceiptRequestDTO.setSerialNumber(humoConnect.getDeviceSerialNumber());
            humoConnectLastReceiptRequestDTO.setTin(humoConnect.getCustomer().getTin());
            humoConnectLastReceiptRequestDTO.setPaymentType(humoConnect.getPaymentType());
            rabbitMqHumoConnectProducer.updateLastReceipt(humoConnectLastReceiptRequestDTO);
        });
    }
}
