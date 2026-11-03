package uz.uzkassa.smartpossupply.service;

import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uz.uzkassa.smartpossupply.domain.XtraceToken;
import uz.uzkassa.smartpossupply.domain.company.Company;
import uz.uzkassa.smartpossupply.domain.xtrace.RedisXtraceToken;
import uz.uzkassa.smartpossupply.dto.xtrace.XtraceCheckApiKeyResponse;
import uz.uzkassa.smartpossupply.dto.xtrace.XtraceTokenDTO;
import uz.uzkassa.smartpossupply.dto.xtrace.XtraceTokenDetailDTO;
import uz.uzkassa.smartpossupply.integration.XtraceClient;
import uz.uzkassa.smartpossupply.repository.XtraceTokenRepository;
import uz.uzkassa.smartpossupply.repository.redis.RedisXtraceTokenRepository;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static lombok.AccessLevel.PRIVATE;

@Service
@Transactional(readOnly = true)
@FieldDefaults(level = PRIVATE, makeFinal = true)
@RequiredArgsConstructor
public class XtraceTokenService extends BaseService {

    XtraceTokenRepository xtraceTokenRepository;
    XtraceClient xtraceClient;
    RedisXtraceTokenRepository redisXtraceTokenRepository;

    @Transactional
    public Long create(XtraceTokenDTO xtraceTokenDTO) {
        Long companyId = Optional.ofNullable(xtraceTokenDTO.getCompanyId()).orElseGet(BaseService::getCurrentCompanyId);
        Company company = companyRepository.findById(companyId).orElseThrow(validationService.entityNotFoundExceptionThrow(Company.class.getSimpleName(), "id", companyId));
        XtraceCheckApiKeyResponse checkApiKeyResponse = xtraceClient.checkApiKey(xtraceTokenDTO.getApiKey(), company.getTin());
        XtraceToken xtraceToken = xtraceTokenRepository.findByCompanyId(company.getId()).orElseGet(XtraceToken::new);
        xtraceToken.setApiKey(xtraceTokenDTO.getApiKey());
        xtraceToken.setCompanyId(company.getId());
        xtraceToken.setExpireDate(Optional.ofNullable(checkApiKeyResponse.expiresOn()).orElseGet(() -> LocalDateTime.now().plusDays(30)));
        xtraceTokenRepository.save(xtraceToken);
        redisXtraceTokenRepository.deleteById(company.getId());
        return xtraceToken.getId();
    }

    public List<XtraceTokenDetailDTO> getAllXtraceTokens() {
        return xtraceTokenRepository.findAllByApiKeyNotNull().stream().map(this::toDetailDTO).toList();
    }

    public XtraceTokenDetailDTO get() {
        Long companyId = getCurrentCompanyId();
        return redisXtraceTokenRepository.findById(companyId)
                .map(this::toDetailDTO)
                .orElseGet(() -> xtraceTokenRepository
                        .findByCompanyId(companyId)
                        .map(xtraceToken -> {
                            cacheToRedis(xtraceToken);
                            return toDetailDTO(xtraceToken);
                        })
                        .orElse(null));
    }

    public void deleteXTraceToken() {
        redisXtraceTokenRepository.deleteById(getCurrentCompanyId());
    }

    public void deleteXTraceToken(Long companyId) {
        redisXtraceTokenRepository.deleteById(companyId);
    }

    private XtraceTokenDetailDTO toDetailDTO(XtraceToken xtraceToken) {
        XtraceTokenDetailDTO dto = new XtraceTokenDetailDTO();
        dto.setCompanyId(xtraceToken.getCompanyId());
        dto.setApiKey(xtraceToken.getApiKey());
        dto.setApiKeyId(xtraceToken.getApiKeyId());
        dto.setExpireDate(xtraceToken.getExpireDate());
        return dto;
    }

    private XtraceTokenDetailDTO toDetailDTO(RedisXtraceToken cached) {
        XtraceTokenDetailDTO dto = new XtraceTokenDetailDTO();
        dto.setCompanyId(cached.getCompanyId());
        dto.setApiKey(cached.getApiKey());
        dto.setApiKeyId(cached.getApiKeyId());
        dto.setExpireDate(cached.getExpireDate());
        return dto;
    }

    private void cacheToRedis(XtraceToken xtraceToken) {
        long ttlSeconds = Duration.between(LocalDateTime.now(), xtraceToken.getExpireDate()).getSeconds();
        if (ttlSeconds <= 0) {
            return;
        }
        RedisXtraceToken cached = new RedisXtraceToken();
        cached.setCompanyId(xtraceToken.getCompanyId());
        cached.setApiKey(xtraceToken.getApiKey());
        cached.setApiKeyId(xtraceToken.getApiKeyId());
        cached.setExpireDate(xtraceToken.getExpireDate());
        cached.setTtl(ttlSeconds);
        redisXtraceTokenRepository.save(cached);
    }
}
