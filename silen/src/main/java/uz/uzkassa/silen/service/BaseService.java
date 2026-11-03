package uz.uzkassa.silen.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.EntityNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.Nullable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Lazy;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Component;
import uz.uzkassa.silen.domain.AsilBelgiToken;
import uz.uzkassa.silen.domain.Organization;
import uz.uzkassa.silen.domain.Settings;
import uz.uzkassa.silen.domain.mongo.Mark;
import uz.uzkassa.silen.domain.mongo.TraceAggregationRequest;
import uz.uzkassa.silen.dto.eimzo.CertificateDto;
import uz.uzkassa.silen.dto.eimzo.SignatureData;
import uz.uzkassa.silen.dto.eimzo.SignerDto;
import uz.uzkassa.silen.dto.invoice.FacturaDTO;
import uz.uzkassa.silen.dto.invoice.FacturaRejectDTO;
import uz.uzkassa.silen.exceptions.BadRequestException;
import uz.uzkassa.silen.integration.AsilBelgisiClient;
import uz.uzkassa.silen.integration.TuronClient;
import uz.uzkassa.silen.integration.asilbelgi.dto.CisInfo;
import uz.uzkassa.silen.integration.asilbelgi.dto.CisInfoResponse;
import uz.uzkassa.silen.integration.turon.CreateAggregationRequest;
import uz.uzkassa.silen.integration.turon.CreateAggregationResponse;
import uz.uzkassa.silen.rabbitmq.producer.RabbitMqProducer;
import uz.uzkassa.silen.repository.mongo.TraceAggregationRepository;
import uz.uzkassa.silen.repository.pg.AsilBelgiTokenRepository;
import uz.uzkassa.silen.repository.pg.OrganizationRepository;
import uz.uzkassa.silen.repository.pg.SettingsRepository;
import uz.uzkassa.silen.security.SecurityUtils;
import uz.uzkassa.silen.transaction.AfterCommitExecutor;
import uz.uzkassa.silen.utils.DateUtils;

import java.io.IOException;
import java.util.*;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collectors;

@Slf4j
@Component
public abstract class BaseService {
    @Autowired
    @Lazy
    private MessageSource messageSource;
    @Autowired
    @Lazy
    protected AfterCommitExecutor afterCommitExecutor;
    @Autowired
    @Lazy
    protected RabbitMqProducer rabbitMqProducer;
    @Autowired
    @Lazy
    protected ObjectMapper objectMapper;
    @Autowired
    @Lazy
    protected OrganizationRepository organizationRepository;
    @Autowired
    @Lazy
    protected SettingsRepository settingsRepository;
    @Autowired
    @Lazy
    protected CacheService cacheService;
    @Autowired
    @Lazy
    protected TuronClient turonClient;
    @Autowired
    @Lazy
    protected AsilBelgisiClient asilBelgisiClient;
    @Autowired
    private TraceAggregationRepository traceAggregationRepository;
    @Autowired
    private AsilBelgiTokenRepository asilBelgiTokenRepository;


    protected static String getCurrentUserId() {
        return SecurityUtils.getCurrentUserId();
    }

    protected static String getCurrentUserLogin() {
        return SecurityUtils.getCurrentUserLogin();
    }

    protected static String getCurrentOrganizationId() {
        return SecurityUtils.getCurrentOrganizationId();
    }

    protected static String getCurrentOrganizationTin() {
        return SecurityUtils.getCurrentOrganizationTin();
    }

    protected Organization getCurrentOrganization() {
        return organizationRepository.findById(getCurrentOrganizationId()).orElseThrow(notFound("Организация не найдена"));
    }

    protected String getCurrentOrganizationAsilBelgiToken() {
        AsilBelgiToken token = asilBelgiTokenRepository.findById(getCurrentOrganizationTin()).orElseThrow(notFound("Турон токен не найден"));
        return token.getToken();
    }

    protected String getCurrentOrganizationAsilBelgiToken(String organizationTin) {
        AsilBelgiToken token = asilBelgiTokenRepository.findById(organizationTin).orElseThrow(notFound("Турон токен не найден"));
        return token.getToken();
    }

    protected String getCurrentOrganizationAsilBelgiTokenOrNul(String organizationTin) {
        Optional<AsilBelgiToken> token = asilBelgiTokenRepository.findById(organizationTin);
        return token.map(AsilBelgiToken::getToken).orElse(null);
    }


    protected String getAppVersion() {
        return settingsRepository.findFirstByOrderById().map(Settings::getAppVersion).orElse(null);
    }

    protected Long getCurrentSessionId() {
        return SecurityUtils.getCurrentSessionId();
    }

    protected Organization getCurrentOrganizationAndValidateOmsIdAndTuronToken() {
        final Organization organization = getCurrentOrganization();
        if (StringUtils.isAnyEmpty(organization.getOmsId(), organization.getTuronToken())) {
            throw badRequest("В данной организации отсутствует OMS_ID или TOKEN").get();
        }
        return organization;
    }

    protected Organization getCurrentOrganizationAndValidateTokens() {
        final Organization organization = getCurrentOrganization();
        if (StringUtils.isAnyEmpty(organization.getOmsId(), organization.getTuronToken())) {
            throw badRequest("В данной организации отсутствует OMS_ID или TOKEN").get();
        }
        return organization;
    }

    protected static boolean isAgency() {
        return getCurrentOrganizationId() == null;
    }

    protected Supplier<EntityNotFoundException> notFound() {
        return EntityNotFoundException::new;
    }

    protected Supplier<EntityNotFoundException> notFound(String message) {
        return () -> new EntityNotFoundException(message);
    }

    protected Supplier<BadRequestException> badRequest(String error) {
        return () -> new BadRequestException(error);
    }

    protected String localize(String code) {
        return this.localize(LocaleContextHolder.getLocale(), code, null);
    }

    protected String localize(String code, Object... params) {
        return localize(LocaleContextHolder.getLocale(), code, params);
    }


    protected String localize(Locale locale, String code, Object[] params) {
        if (StringUtils.isEmpty(code)) {
            return null;
        }
        return messageSource.getMessage(code, params, locale);
    }


    protected FacturaDTO parseBase64(final String base64) {
        final String json = new String(Base64.getDecoder().decode(base64));
        FacturaDTO result = null;
        try {
            result = objectMapper.readValue(json, FacturaDTO.class);
        } catch (IOException e) {
            log.error(e.getMessage());
            try {
                FacturaRejectDTO facturaRejectDTO = objectMapper.readValue(json, FacturaRejectDTO.class);
                result = facturaRejectDTO.getFactura();
                result.setNotes(facturaRejectDTO.getNotes());
            } catch (IOException ex) {
                log.info("JSON: {}", json);
                log.error(ex.getMessage());
            }
        }
        return result;
    }

    protected SignatureData parseSignatureData(final SignerDto signerDto) {
        final CertificateDto certificateDto = signerDto.getCertificate().getFirst();
        final SignatureData data = new SignatureData();
        data.setNumber(String.valueOf(Integer.parseInt(certificateDto.getSerialNumber(), 16)));
        data.setOwner(certificateDto.getSubjectInfo().getFullName());
        data.setDateTime(DateUtils.parse(signerDto.getTimeStampInfo().getTime()));
        return data;
    }

    public CreateAggregationResponse sendAggregationToTuronTrace(CreateAggregationRequest request, String extension, String clientToken, String omsId) {
        TraceAggregationRequest traceAggregationRequest = new TraceAggregationRequest();
        traceAggregationRequest.setRequest(request);
        traceAggregationRequest.setExtension(extension);
        traceAggregationRequest.setClientToken(clientToken);
        traceAggregationRequest.setOmsId(omsId);

        try {
            CreateAggregationResponse response = turonClient.createAggregation(request, extension, clientToken, omsId);
            traceAggregationRequest.setResponse(response);
            traceAggregationRepository.save(traceAggregationRequest);
            return response;

        } catch (Exception e) {
            traceAggregationRequest.setResponse(new CreateAggregationResponse(null, e.getMessage()));
            traceAggregationRepository.save(traceAggregationRequest);
            log.error("Aggregation request", e);
            throw e;
        }
    }

    public Map<String, CisInfoResponse> checkFromAsilBelgisi(Set<String> codes, final String organizationTrueToken) {
        if (codes.isEmpty()) {
            return Collections.emptyMap();
        }
        final CisInfoResponse[] result = asilBelgisiClient.cisesInfo(organizationTrueToken, codes);
        return Arrays.stream(result)
                .filter(response -> response != null && response.getCisInfo() != null)
                .collect(Collectors.toMap(response -> response.getCisInfo().getRequestedCis(), Function.identity()));
    }

    protected @Nullable String checkResponse(final String code, final Map<String, CisInfoResponse> cisInfoMap, String time) {
        if (!cisInfoMap.containsKey(code)) {
            return localize("checked.error", time, "Не найден информация");
        }
        CisInfoResponse cisInfoResponse = cisInfoMap.getOrDefault(code, null);
        if (cisInfoResponse == null) {
            return localize("checked.error", time, "Не найден информация");
        }
        if (cisInfoResponse.getErrorMessage() != null) {
            return localize("checked.error", time, cisInfoResponse.getErrorMessage());
        }
        if (cisInfoResponse.getCisInfo() == null) {
            return localize("checked.error", time, "Не найден информация");
        }
        CisInfo cisInfo = cisInfoResponse.getCisInfo();
        if (CollectionUtils.isEmpty(cisInfo.getChild())) {
            return localize("checked.error", time, "Не найден состав, статус: " + cisInfo.getStatusName());
        }
        return null;
    }

    protected void printInfo(String message) {
        log.info("\n==========================================\n{}\n==========================================\n", message);
    }

    protected void printError(String message) {
        log.info("\n==========================================\n{}\n==========================================\n", message);
    }

    protected Collection<String> marksToCode(final List<Mark> marks) {
        return marks.stream().map(Mark::getCode).collect(Collectors.toSet());
    }
}
