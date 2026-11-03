package uz.uzkassa.cts.jobs;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.context.annotation.Profile;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.FileCopyUtils;
import uz.uzkassa.cts.config.ApplicationProperties;
import uz.uzkassa.cts.constants.FileConstants;
import uz.uzkassa.cts.dto.payload.OfdApplicationSendPayload;
import uz.uzkassa.cts.rabbitmq.producer.RabbitMqOfdProducer;
import uz.uzkassa.cts.service.FiscalizationService;

import java.io.InputStream;
import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;

@Component
@Slf4j
@Profile("prod")
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
@RequiredArgsConstructor
public class ApplicationSyncCronJob {

    ApplicationProperties applicationProperties;
    RabbitMqOfdProducer rabbitMqOfdProducer;
    FiscalizationService fiscalizationService;

    /**
     * Send new applications to OFD
     * 1 hour. Run after 15 min
     */
    @Scheduled(fixedRate = 3600000, initialDelay = 900000)
    @Profile("prod")
    @Transactional
    public void sendApplications() {
        ArrayList<BigInteger> fiscalizationIds = fiscalizationService.getListToSendOfd();
        if (CollectionUtils.isNotEmpty(fiscalizationIds)) {
            try {
                String baseUrl = applicationProperties.getOfdProperty().getCustomerFiscalAddUrl();
                Resource resource = new ClassPathResource("/data/contract_offer_base64.txt");
                InputStream inputStream = resource.getInputStream();
                String file64 = new String(FileCopyUtils.copyToByteArray(inputStream), StandardCharsets.UTF_8);
                fiscalizationIds.forEach(fiscalizationId -> {
                    OfdApplicationSendPayload ofdApplicationSendPayload = new OfdApplicationSendPayload();
                    ofdApplicationSendPayload.setFiscalizationId(fiscalizationId.longValue());
                    ofdApplicationSendPayload.setApiUrl(baseUrl);
                    ofdApplicationSendPayload.setContractOfferBase64(file64);
                    ofdApplicationSendPayload.setContractOfferName(FileConstants.CONTRACT_OFFER_FILE_NAME);
                    rabbitMqOfdProducer.sendApplications(ofdApplicationSendPayload);
                });
            } catch (Exception e) {
                log.error("Contract offer File parse error:{}", e.getMessage());
                log.error("File url:" + FileConstants.CONTRACT_OFFER_FILE_URL);
            }
        }
    }
}
