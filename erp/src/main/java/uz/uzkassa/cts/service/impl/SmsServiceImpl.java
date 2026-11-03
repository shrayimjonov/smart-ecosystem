package uz.uzkassa.cts.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;
import uz.uzkassa.cts.config.ApplicationProperties;
import uz.uzkassa.cts.domain.Sms;
import uz.uzkassa.cts.dto.sms.EtcEntityDTO;
import uz.uzkassa.cts.dto.sms.EtcRequestDTO;
import uz.uzkassa.cts.enums.ProviderType;
import uz.uzkassa.cts.enums.SmsStatus;
import uz.uzkassa.cts.repository.SmsRepository;
import uz.uzkassa.cts.service.SmsService;
import uz.uzkassa.cts.web.rest.errors.IntegrationException;

import static lombok.AccessLevel.PRIVATE;
import static uz.uzkassa.cts.security.SecurityUtils.getHeader;

@Service
@Slf4j
@FieldDefaults(level = PRIVATE, makeFinal = true)
@RequiredArgsConstructor
@Transactional
public class SmsServiceImpl implements SmsService {

    ApplicationProperties applicationProperties;
    RestTemplate restTemplate;
    SmsRepository smsRepository;

    @Override
    public boolean send(EtcRequestDTO etcRequestDTO) {
        ApplicationProperties.EtcSmsProperty config = applicationProperties.getEtcSmsProperty();

        EtcEntityDTO etcEntityDto = EtcEntityDTO.builder()
                .login(config.getUsername())
                .pwd(config.getPassword())
                .cdPN(etcRequestDTO.getPhone())
                .text(etcRequestDTO.getText())
                .cgPN(config.getCgpn())
                .build();

        HttpEntity<EtcEntityDTO> entity = new HttpEntity<>(etcEntityDto, getHeader());
        try {
            ResponseEntity<String> response = restTemplate.exchange(config.getHost().concat("/json2sms"), HttpMethod.POST, entity, String.class);
            boolean isSent = HttpStatus.OK == response.getStatusCode();
            saveSms(etcRequestDTO.getPhone(), etcRequestDTO.getText(), isSent ? SmsStatus.SENT : SmsStatus.FAIL);
            return isSent;
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            saveSms(etcRequestDTO.getPhone(), etcRequestDTO.getText(), SmsStatus.FAIL);
            return false;
        }
    }

    @Override
    public void checkConnection() {
        ApplicationProperties.EtcSmsProperty config = applicationProperties.getEtcSmsProperty();

        EtcEntityDTO etcEntityDto = EtcEntityDTO.builder()
                .login(config.getUsername())
                .pwd(config.getPassword())
                .cdPN(applicationProperties.getSmsProperty().getCheckPhone())
                .text("Checking Sms service")
                .cgPN(config.getCgpn())
                .build();
        try {
            HttpEntity<EtcEntityDTO> entity = new HttpEntity<>(etcEntityDto, getHeader());
            ResponseEntity<String> response = restTemplate.exchange(config.getHost().concat("/json2sms"), HttpMethod.POST, entity, String.class);

            if (response.getStatusCode().value() != 200) {
                throw new IntegrationException("An unexpected error has occurred while authenticating to Sms service. Error: " + response, null, HttpStatus.valueOf(response.getStatusCode().value()));
            }
        } catch (Exception ex) {
            log.error(ex.getMessage(), ex);
            throw new IntegrationException("An unexpected error has occurred while authenticating to Sms service. Error: " + ex.getMessage(), null, HttpStatus.BAD_REQUEST);
        }
    }

    private void saveSms(String phone, String text, SmsStatus status) {
        Sms sms = new Sms();
        sms.setMessage(text);
        sms.setProviderType(ProviderType.ETC);
        sms.setRecipient(phone);
        sms.setStatus(status);
        smsRepository.save(sms);
    }

}
