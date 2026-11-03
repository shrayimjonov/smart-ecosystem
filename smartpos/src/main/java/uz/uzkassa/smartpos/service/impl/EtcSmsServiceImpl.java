package uz.uzkassa.smartpos.service.impl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import uz.uzkassa.smartpos.config.ApplicationProperties;
import uz.uzkassa.smartpos.domain.postgres.Sms;
import uz.uzkassa.smartpos.dto.etcsms.request.EtcEntityDto;
import uz.uzkassa.smartpos.dto.etcsms.request.EtcRequestDto;
import uz.uzkassa.smartpos.enums.ProviderTypeEnum;
import uz.uzkassa.smartpos.enums.SmsStatusEnum;
import uz.uzkassa.smartpos.repository.postgres.SmsRepository;
import uz.uzkassa.smartpos.service.EtcSmsService;

import static uz.uzkassa.smartpos.security.SecurityUtils.getHeader;

@Component
@Slf4j
public class EtcSmsServiceImpl implements EtcSmsService {
    private final RestTemplate restTemplate;
    private final ApplicationProperties applicationProperties;
    private final SmsRepository smsRepository;
    private static final String SENT_JSON_REQUEST = "/json2sms";

    @Autowired
    public EtcSmsServiceImpl(RestTemplate restTemplate, ApplicationProperties applicationProperties, SmsRepository smsRepository) {
        this.restTemplate = restTemplate;
        this.applicationProperties = applicationProperties;
        this.smsRepository = smsRepository;
    }

    @Override
    public SmsStatusEnum sendSms(EtcRequestDto requestDto) {
        ApplicationProperties.EtcSms config = applicationProperties.getEtcSms();
        requestDto.setText(requestDto.getText() + "\nsmartpos.uz");
        EtcEntityDto.EtcEntityDtoBuilder builder = EtcEntityDto.builder()
                .login(config.getUsername())
                .pwd(config.getPassword())
                .cdPN(requestDto.getPhone())
                .text(requestDto.getText())
                .cgPN(config.getCgpn());

        try {
            HttpEntity<EtcEntityDto> entity = new HttpEntity<>(builder.build(), getHeader());
            ResponseEntity<String> response = restTemplate.exchange(config.getHost() + SENT_JSON_REQUEST, HttpMethod.POST, entity, String.class);
            SmsStatusEnum status = response.getStatusCode().value() == 200 ? SmsStatusEnum.SENT : SmsStatusEnum.FAIL;
            saveSms(requestDto.getPhone(), requestDto.getText(), status);
            return status;
        } catch (Exception ex) {
            log.error(ex.getMessage());
            saveSms(requestDto.getPhone(), requestDto.getText(), SmsStatusEnum.FAIL);
            return SmsStatusEnum.FAIL;
        }
    }

    @Override
    public void sendSmsForReceipt(EtcRequestDto requestDto) {
        ApplicationProperties.EtcSms config = applicationProperties.getEtcSms();
        EtcEntityDto.EtcEntityDtoBuilder builder = EtcEntityDto.builder()
                .login(config.getUsername())
                .pwd(config.getPassword())
                .cdPN(requestDto.getPhone())
                .text(requestDto.getText())
                .cgPN(config.getCgpn());

        try {
            HttpEntity<EtcEntityDto> entity = new HttpEntity<>(builder.build(), getHeader());
            ResponseEntity<String> response = restTemplate.exchange(config.getHost() + SENT_JSON_REQUEST, HttpMethod.POST, entity, String.class);
            SmsStatusEnum status = response.getStatusCode().value() == 200 ? SmsStatusEnum.SENT : SmsStatusEnum.FAIL;
            saveSms(requestDto.getPhone(), requestDto.getText(), status);
        } catch (Exception ex) {
            log.error(ex.getMessage());
            saveSms(requestDto.getPhone(), requestDto.getText(), SmsStatusEnum.FAIL);
        }
    }

    @Override
    public void saveSms(String phone, String text, SmsStatusEnum status) {
        Sms sms = new Sms();
        sms.setMessage(text);
        sms.setProviderType(ProviderTypeEnum.ETC);
        sms.setRecipient(phone);
        sms.setStatus(status);
        smsRepository.save(sms);
    }
}
