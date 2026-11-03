package uz.uzkassa.silen.service;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import uz.uzkassa.silen.domain.AsilBelgiToken;
import uz.uzkassa.silen.domain.Organization;
import uz.uzkassa.silen.dto.CheckCisesDTO;
import uz.uzkassa.silen.dto.eimzo.Pkcs7Dto;
import uz.uzkassa.silen.exceptions.BadRequestException;
import uz.uzkassa.silen.integration.EimzoClient;
import uz.uzkassa.silen.integration.SupplierClient;
import uz.uzkassa.silen.integration.asilbelgi.dto.AuthKey;
import uz.uzkassa.silen.integration.asilbelgi.dto.CisInfoResponse;
import uz.uzkassa.silen.integration.asilbelgi.dto.ReportInfo;
import uz.uzkassa.silen.integration.asilbelgi.dto.TrueApiRequest;
import uz.uzkassa.silen.integration.supplier.CommittentTokenBatchDTO;
import uz.uzkassa.silen.integration.supplier.CommittentTokenDTO;
import uz.uzkassa.silen.repository.pg.AsilBelgiTokenRepository;
import uz.uzkassa.silen.repository.pg.CustomerRepository;

import java.time.LocalDateTime;
import java.util.*;

@Slf4j
@Getter
@Setter
@Service
@RequiredArgsConstructor
public class AsilBelgiService extends BaseService {
    private final EimzoClient eimzoClient;
    private final AsilBelgiTokenRepository asilBelgiTokenRepository;
    private final CustomerRepository customerRepository;
    private final SupplierClient supplierClient;
    private final TelegramBotApi botApi;

    public AuthKey getAuthKey() {
        return asilBelgisiClient.getAuthKey();
    }

    public String signIn(final AuthKey key) {
        final Pkcs7Dto pkcs7Dto = eimzoClient.frontTimestamp(key.getData());
        if (pkcs7Dto.isSuccess()) {
            key.setData(pkcs7Dto.getPkcs7b64());
            final String token = asilBelgisiClient.signIn(key);
            updateTrueToken(new AuthKey(getCurrentOrganizationTin(), token));
            return token;
        } else {
            log.error("JSON: {}", pkcs7Dto);
            throw new BadRequestException("Not verified, reason: " + pkcs7Dto.getMessage());
        }
    }

    public String updateTrueToken(final AuthKey data) {
        AsilBelgiToken organization = asilBelgiTokenRepository.findById(data.getUuid()).orElseGet(AsilBelgiToken::new);
        organization.setToken(data.getData());
        organization.setTin(data.getUuid());
        organization.setTokenDate(LocalDateTime.now());
        asilBelgiTokenRepository.save(organization);

        afterCommitExecutor.execute(() -> {
            try {
                List<String> committents = customerRepository.findAllCommittentsTinByOrganizationTinAndComissionIsTrue(organization.getTin());
                if (CollectionUtils.isNotEmpty(committents)) {
                    CommittentTokenBatchDTO committentsBatch = new CommittentTokenBatchDTO();
                    committentsBatch.getCompanies().add(new CommittentTokenDTO(organization.getTin(), organization.getToken(), committents));
                    supplierClient.send(committentsBatch);
                }
            } catch (Exception e) {
                log.error("Sending committent tokens error", e);
            }
        });
        return organization.getTin();
    }

    public CisInfoResponse[] cisesInfo(CheckCisesDTO checkCisesDTO) {
        String token;
        if (checkCisesDTO.getOrganizationId() != null) {
            final Organization organization = organizationRepository.findById(checkCisesDTO.getOrganizationId()).orElseThrow(notFound());
            token = getCurrentOrganizationAsilBelgiToken(organization.getTin());
        } else {
            token = getCurrentOrganizationAsilBelgiToken();
        }
        if (StringUtils.isEmpty(token)) {
            throw badRequest("Отсутствует токен организации").get();
        }
        return asilBelgisiClient.cisesInfo(token, checkCisesDTO.getCodes());
    }

    public Set<String> aggregatedList(TrueApiRequest request) {
        final String organizationId = request.getOrganizationId() == null ? getCurrentOrganizationId() : request.getOrganizationId();
        final Set<String> codes = request.getCodes() == null ? Collections.singleton(request.getCode()) : request.getCodes();
        Organization organization = organizationRepository.findById(organizationId).orElseThrow(notFound());
        final String token = getCurrentOrganizationAsilBelgiToken(organization.getTin());
        Map<String, Map<String, List>> result = asilBelgisiClient.aggregatedList(token, organization.getProductGroup(), codes);
        if (result != null && !result.keySet().isEmpty()) {
            for (String key : result.keySet()) {
                Map<String, List> map = result.get(key);
                return map.keySet();
            }
        }
        return null;
    }

    public ReportInfo getReportInfo(String organizationId, String id) {
        final String orgId = Optional.ofNullable(organizationId).orElse(getCurrentOrganizationId());
        final Organization organization = organizationRepository.findById(orgId).orElseThrow(notFound("Organization not found"));
        return asilBelgisiClient.getReportInfo(getCurrentOrganizationAsilBelgiToken(organization.getTin()), id);
    }

    public Object getCisesHistory(String code) {
        return asilBelgisiClient.getCisesHistory(getCurrentOrganizationAsilBelgiToken(), code);
    }

    public Object getProductGroupBalance() {
        final Organization organization = getCurrentOrganization();
        if (organization.getProductGroup() == null) {
            throw new BadRequestException("Товарный группа не найден");
        }
        return asilBelgisiClient.getProductGroupBalance(getCurrentOrganizationAsilBelgiToken(organization.getTin()), organization.getProductGroup().getProductGroup());
    }

    public Object getDocInfo(String id) {
//        final Organization organization = getCurrentOrganization();
//        if (StringUtils.isEmpty(organization.getTrueToken())) {
//            throw badRequest("Турон токен не найден").get();
//        }
        return asilBelgisiClient.getDocInfo(getCurrentOrganizationAsilBelgiToken(), id);
    }

    public void refreshTokens() {
        LocalDateTime now = LocalDateTime.now().minusHours(1);
        List<AsilBelgiToken> organizations = asilBelgiTokenRepository.findAllByTokenDateBefore(LocalDateTime.now().minusHours(8));
        if (CollectionUtils.isEmpty(organizations)) {
            log.info("Organizations list is empty");
            return;
        }

        Map<String, String> updatedOrganizations = new HashMap<>();
        for (AsilBelgiToken organization : organizations) {
            try {
                String token = asilBelgisiClient.refresh(organization.getToken());
                organization.setToken(token);
                organization.setTokenDate(now);
                asilBelgiTokenRepository.save(organization);

                log.info("Organization {} refreshed successfully", organization.getTin());
                updatedOrganizations.put(organization.getTin(), token);
                // collecting data
            } catch (Exception e) {
                String url = asilBelgisiClient.getStartEndpoint() + "auth/refresh";
                StringBuilder message = new StringBuilder();
                message.append("REFRESH TOKEN ERROR: \n");
                message.append(url).append("\n");
                message.append("TIN: ").append(organization.getTin()).append("\n");
                message.append("TOKEN: ").append(organization.getToken()).append("\n");
                message.append("TOKEN DATE: ").append(organization.getTokenDate()).append("\n");
                message.append("MESSAGE: ").append(e.getMessage()).append("\n");
                log.debug("{} Can't get token", message);
                botApi.sendMessageToAdminChannel(message.toString());
            }
        }

        try {
            if (!updatedOrganizations.isEmpty()) {
                StringBuilder builder = new StringBuilder();
                builder.append("REFRESH TOKEN \n");
                builder.append("==================== \n");
                builder.append("INN LIST SIZE: ").append(updatedOrganizations.size()).append(" \n");
                builder.append("INN LIST:  \n");
                builder.append(updatedOrganizations.keySet());
                builder.append("\n");
                builder.append("URL: /auth/refresh \n");
                builder.append(asilBelgisiClient.getStartEndpoint());
                builder.append("TOKEN: refresh token \n");
                log.info(builder.toString());
                botApi.sendMessageToAdminChannel(builder.toString());
            }
            CommittentTokenBatchDTO committentsBatch = new CommittentTokenBatchDTO();
            for (String tin : updatedOrganizations.keySet()) {
                List<String> committents = customerRepository.findAllCommittentsTinByOrganizationTinAndComissionIsTrue(tin);
                if (CollectionUtils.isNotEmpty(committents)) {
                    committentsBatch.getCompanies().add(new CommittentTokenDTO(tin, updatedOrganizations.get(tin), committents));
                }
            }
            if (CollectionUtils.isNotEmpty(committentsBatch.getCompanies())) {
                supplierClient.send(committentsBatch);
            }
        } catch (Exception e) {
            log.error("Sending committent tokens error", e);
        }
    }

    public Integer refreshTokensOnce() {
        LocalDateTime now = LocalDateTime.now().minusHours(1);
        List<Organization> organizations = organizationRepository.findAllByTrueTokenIsNotNull();
        if (CollectionUtils.isNotEmpty(organizations)) {
            for (Organization organization : organizations) {
                try {
                    String token = asilBelgisiClient.refresh(organization.getTrueToken());
                    AsilBelgiToken asilBelgiToken = asilBelgiTokenRepository.findById(organization.getTin()).orElseGet(AsilBelgiToken::new);
                    asilBelgiToken.setToken(token);
                    asilBelgiToken.setTokenDate(now);
                    asilBelgiToken.setTin(organization.getTin());
                    asilBelgiTokenRepository.save(asilBelgiToken);
                    log.info("Organization {} refreshed successfully", organization.getName());
                } catch (Exception e) {
                    log.error("Sync tokens error {}", organization.getTin(), e);
                }
            }
        }
        return organizations.size();
    }
}
