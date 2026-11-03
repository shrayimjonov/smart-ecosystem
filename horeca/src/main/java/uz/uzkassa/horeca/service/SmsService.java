package uz.uzkassa.horeca.service;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;
import uz.uzkassa.horeca.domain.SmsHistory;
import uz.uzkassa.horeca.dto.sms.EtcEntityDTO;
import uz.uzkassa.horeca.dto.sms.SmsRequestDTO;
import uz.uzkassa.horeca.repository.SmsHistoryRepository;
import uz.uzkassa.horeca.utils.HeaderUtils;

@Service
@Slf4j
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
@RequiredArgsConstructor
public class SmsService extends BaseService {

    SmsHistoryRepository smsHistoryRepository;
    RestTemplate restTemplate;

    @Transactional
    public void send(SmsRequestDTO smsRequestDTO) {
        String phoneNumber = smsRequestDTO.getPhone().replace("+", "").trim();
        String message = smsRequestDTO.getCode();
        if (StringUtils.isEmpty(message)) {
            return;
        }
        if (phoneNumber.startsWith("99898")) {
            message = message + " \na-pay.uz";
        }

        EtcEntityDTO.EtcEntityDTOBuilder etcEntity = EtcEntityDTO
                .builder()
                .login(applicationProperties.getSmsConfig().getUsername())
                .pwd(applicationProperties.getSmsConfig().getPassword())
                .cdPN(phoneNumber)
                .cgPN(applicationProperties.getSmsConfig().getCgpn())
                .text(message);
        try {
            HttpEntity<EtcEntityDTO> entity = new HttpEntity<>(etcEntity.build(), HeaderUtils.getHeader());

            UriComponentsBuilder uriBuilder = UriComponentsBuilder
                    .fromUriString(applicationProperties.getSmsConfig().getHost())
                    .path("/json2sms");

            restTemplate.exchange(uriBuilder.toUriString(), HttpMethod.POST, entity, String.class);
            create(phoneNumber, smsRequestDTO.getCode(), message, null);
        } catch (Exception e) {
            log.error(e.getMessage());
            create(phoneNumber, smsRequestDTO.getCode(), message, e.getMessage());
            badRequestExceptionThrow("Sms sending failed").get();
        }
    }

    private void create(String phone, String code, String message, String responseMessage) {
        SmsHistory history = new SmsHistory();
        history.setPhone(phone);
        history.setMessage(message);
        history.setCode(code);
        history.setResponseMessage(responseMessage);
        smsHistoryRepository.save(history);
    }
}
