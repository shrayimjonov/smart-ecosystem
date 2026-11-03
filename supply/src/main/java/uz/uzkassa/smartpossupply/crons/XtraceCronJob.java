package uz.uzkassa.smartpossupply.crons;

import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Profile;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import uz.uzkassa.smartpossupply.dto.xtrace.XtraceRefreshApiKeyResponse;
import uz.uzkassa.smartpossupply.integration.XtraceClient;
import uz.uzkassa.smartpossupply.repository.XtraceTokenRepository;
import uz.uzkassa.smartpossupply.repository.redis.RedisXtraceTokenRepository;

import java.time.LocalDateTime;
import java.util.Optional;

import static lombok.AccessLevel.PRIVATE;

@Component
@Slf4j
@Profile("prod")
@FieldDefaults(level = PRIVATE, makeFinal = true)
@RequiredArgsConstructor
public class XtraceCronJob {

    XtraceTokenRepository xtraceTokenRepository;
    XtraceClient xtraceClient;
    RedisXtraceTokenRepository redisXtraceTokenRepository;

    @Scheduled(cron = "0 0 5 * * *", zone = "Asia/Tashkent")
    public void refreshToken() {
        xtraceTokenRepository.findAllByExpireDateBetween(LocalDateTime.now(), LocalDateTime.now().plusDays(3))
            .forEach(xtraceToken -> {
                try {
                    XtraceRefreshApiKeyResponse response = xtraceClient.refreshApiKey(xtraceToken.getApiKey(), xtraceToken.getCompany().getTin());
                    xtraceToken.setApiKey(response.apiKey());
                    xtraceToken.setApiKeyId(response.id());
                    xtraceToken.setExpireDate(Optional.ofNullable(response.expiresOn()).orElse(LocalDateTime.now().plusDays(30)));
                    xtraceTokenRepository.save(xtraceToken);
                    redisXtraceTokenRepository.deleteById(xtraceToken.getCompanyId());
                } catch (Exception e) {
                    log.error("Xtrace refresh API key Error, TIN: {}", xtraceToken.getCompany().getTin(), e);
                }
            });
    }
}
