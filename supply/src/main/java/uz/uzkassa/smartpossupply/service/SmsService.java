package uz.uzkassa.smartpossupply.service;

import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;
import uz.uzkassa.smartpossupply.domain.SmsHistory;
import uz.uzkassa.smartpossupply.domain.auth.UserAuth;
import uz.uzkassa.smartpossupply.dto.sms.EtcEntityDTO;
import uz.uzkassa.smartpossupply.dto.sms.SmsDTO;
import uz.uzkassa.smartpossupply.enums.SmsStatus;
import uz.uzkassa.smartpossupply.repository.SmsHistoryRepository;
import uz.uzkassa.smartpossupply.repository.UserAuthRepository;
import uz.uzkassa.smartpossupply.utils.OtpEncryptionUtils;
import uz.uzkassa.smartpossupply.utils.SecurityUtils;

import java.time.LocalDateTime;

import static lombok.AccessLevel.PRIVATE;

@Service
//@Transactional
@FieldDefaults(level = PRIVATE, makeFinal = true)
@RequiredArgsConstructor
@Slf4j
public class SmsService extends BaseService {

    SmsHistoryRepository smsHistoryRepository;
    UserAuthRepository userAuthRepository;
    RestTemplate restTemplate;
    OtpEncryptionUtils otpEncryptionUtils;

    @Transactional
    public boolean send(SmsDTO smsDTO) {
        String phone = normalize(smsDTO.getPhone());
        return send(phone, smsDTO.getMessage());
    }

    @Transactional
    public void sendActivationCode(SmsDTO smsDTO) {
        String phone = normalize(smsDTO.getPhone());
        String code = SecurityUtils.generateActivationKey();
        String message = "Kod aktivatsii: " + code;
        send(phone, message);

        UserAuth userAuth = userAuthRepository.findFirstByUsername(phone).orElse(new UserAuth());
        userAuth.setUsername(phone);
        userAuth.setActivationCode(otpEncryptionUtils.encrypt(code));
        userAuth.setExpiryDate(LocalDateTime.now().plusMinutes(2));
        userAuthRepository.save(userAuth);
    }

    private boolean send(String phone, String message) {
        EtcEntityDTO.EtcEntityDTOBuilder builder = EtcEntityDTO
            .builder()
            .login(applicationProperties.getSmsConfig().getUsername())
            .pwd(applicationProperties.getSmsConfig().getPassword())
            .cdPN(phone)
            .cgPN(applicationProperties.getSmsConfig().getCgpn())
            .text(message + "\nsupply.smartpos.uz");

        try {
            HttpEntity<EtcEntityDTO> entity = new HttpEntity<>(builder.build(), SecurityUtils.getHeader());
            restTemplate.exchange(applicationProperties.getSmsConfig().getHost() + "/json2sms", HttpMethod.POST, entity, String.class);
            saveSms(phone, message, SmsStatus.SENT);
            return true;
        } catch (Exception ex) {
            log.error("SMS send failed", ex);
            saveSms(phone, message, SmsStatus.FAILED);
            return false;
        }
    }

    private static String normalize(String phone) {
        return phone.replace("+", "").trim();
    }

    private void saveSms(String phone, String message, SmsStatus status) {
        SmsHistory history = new SmsHistory();
        history.setPhone(phone);
        history.setMessage(message);
        history.setStatus(status);
        smsHistoryRepository.save(history);
    }
}
