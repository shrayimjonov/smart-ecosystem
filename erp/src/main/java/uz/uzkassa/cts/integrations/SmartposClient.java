package uz.uzkassa.cts.integrations;

import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.AmqpRejectAndDontRequeueException;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import uz.uzkassa.cts.dto.auth.AuthenticateDTO;
import uz.uzkassa.cts.dto.auth.IdTokenDTO;
import uz.uzkassa.cts.dto.banktariff.BankTariffHistoryDTO;
import uz.uzkassa.cts.dto.cases.CaseSmartposAppTypeChange;
import uz.uzkassa.cts.dto.cases.CaseSmartposLoginChangeDto;
import uz.uzkassa.cts.dto.cases.CaseSmartposOwnerChangeDto;
import uz.uzkassa.cts.dto.payload.SmartPosRemoveDevicePayload;
import uz.uzkassa.cts.dto.smartpos.SmartposFiscalizationDetailDTO;
import uz.uzkassa.cts.dto.smartpos.SmartposLastReceiptResponseDTO;
import uz.uzkassa.cts.dto.smartpos.SmartposManagerDTO;
import uz.uzkassa.cts.dto.smartpos.SmartposSyncCustomerBranchDTO;
import uz.uzkassa.cts.dto.smartpos.company.CustomerCtoDTO;
import uz.uzkassa.cts.dto.sms.ReceiptSmsRequestDTO;
import uz.uzkassa.cts.enums.IntegrationType;
import uz.uzkassa.cts.security.SecurityUtils;
import uz.uzkassa.cts.web.rest.errors.IntegrationException;

@Component
@Slf4j
public class SmartposClient extends BaseClient {

    @Override
    public String authenticate() {
//        Integration integration = integrationRepository.findFirstByTypeAndDeletedIsFalse(getIntegrationType()).orElseThrow(validationService.entityNotFoundExceptionThrow(Integration.class.getSimpleName()));
        AuthenticateDTO authenticateDTO = new AuthenticateDTO();
        authenticateDTO.setUsername(applicationProperties.getSmartposProperty().getUsername());
        authenticateDTO.setPassword(applicationProperties.getSmartposProperty().getPassword());
        HttpEntity<AuthenticateDTO> entity = new HttpEntity<>(authenticateDTO, SecurityUtils.getHeader());
        ResponseEntity<IdTokenDTO> response;
        try {
            response = restTemplate.exchange(
                    applicationProperties.getSmartposProperty().getAuthenticateUrl(),
                    HttpMethod.POST,
                    entity,
                    IdTokenDTO.class
            );
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw new IntegrationException("An unexpected error occurred while authenticating to Smartpos service. Error:" + e.getMessage(), null, HttpStatus.BAD_REQUEST);
        }
        if (HttpStatus.OK == response.getStatusCode() && response.getBody() != null) {
            String token = response.getBody().getIdToken();
            tokenStoreService.create(getIntegrationType(), token, applicationProperties.getSmartposProperty().getTokenExpiration());
            return token;
        }
        throw new IntegrationException("An unexpected error occurred while authenticating to Smartpos service. Error:" + response, null, HttpStatus.valueOf(response.getStatusCode().value()));
    }

    @Override
    public String getToken() {
        String token = tokenStoreService.getToken(getIntegrationType());
        if (token != null) {
            return token;
        }
        return authenticate();
    }

    @Override
    public HttpHeaders getHeaders() {
        HttpHeaders headers = SecurityUtils.getHeader();
        headers.setBearerAuth(getToken());
        return headers;
    }

    @Override
    public IntegrationType getIntegrationType() {
        return IntegrationType.SMARTPOS;
    }

    public void fiscalSmsSend(ReceiptSmsRequestDTO receiptSmsRequestDTO) {
        restTemplate.exchange(
                applicationProperties.getSmartposProperty().getSmsSendUrl(),
                HttpMethod.POST,
                new HttpEntity<>(receiptSmsRequestDTO, getHeaders()),
                Void.class
        );
    }

    public void changeLogin(CaseSmartposLoginChangeDto dto) {
        restTemplate.exchange(
                applicationProperties.getSmartposProperty().getChangeLogin(),
                HttpMethod.POST,
                new HttpEntity<>(dto, getHeaders()),
                Void.class
        );
    }

    public void changeAppType(CaseSmartposAppTypeChange dto) {
        restTemplate.exchange(
                applicationProperties.getSmartposProperty().getChangeAppType(),
                HttpMethod.PUT,
                new HttpEntity<>(dto, getHeaders()),
                Void.class
        );
    }

    public boolean checkLoginExists(String login) {
        ResponseEntity<Boolean> response = restTemplate.exchange(
                applicationProperties.getSmartposProperty().getCheckLoginExists() + "?login=" + login,
                HttpMethod.GET,
                new HttpEntity<>(getHeaders()),
                Boolean.class
        );
        if (HttpStatus.OK == response.getStatusCode()) {
            return response.getBody() != null ? response.getBody() : false;
        }
        return false;
    }

    public void changeOwner(CaseSmartposOwnerChangeDto dto) {
        restTemplate.exchange(
                applicationProperties.getSmartposProperty().getChangeOwner(),
                HttpMethod.POST,
                new HttpEntity<>(dto, getHeaders()),
                Void.class
        );
    }

    public void removeDevice(SmartPosRemoveDevicePayload smartPosRemoveDevicePayload) {
        restTemplate.exchange(
                applicationProperties.getSmartposProperty().getRemoveDeviceUrl(),
                HttpMethod.PUT,
                new HttpEntity<>(smartPosRemoveDevicePayload, getHeaders()),
                Void.class
        );
    }

    public void updateCustomer(SmartposFiscalizationDetailDTO fiscalizationDetailSmartposDTO) {
        HttpEntity<SmartposFiscalizationDetailDTO> entity = new HttpEntity<>(fiscalizationDetailSmartposDTO, getHeaders());
        restTemplate.postForEntity(applicationProperties.getSmartposProperty().getUpdateCustomerUrl(), entity, Void.class);
    }

    public CustomerCtoDTO getCustomerByTin(String tin) {
        HttpEntity<CustomerCtoDTO> request = new HttpEntity<>(getHeaders());
        ResponseEntity<CustomerCtoDTO> response = restTemplate.exchange(applicationProperties.getSmartposProperty().getCompanyByTinUrl().concat("/").concat(tin), HttpMethod.GET, request, CustomerCtoDTO.class);
        return response.getBody();
    }

    public void syncCustomerBranch(SmartposSyncCustomerBranchDTO smartposSyncCustomerBranchDTO) {
        try {
            log.info("Request to Smartpos. TIN: {} Customer branch name: {}", smartposSyncCustomerBranchDTO.getCustomer().getTin(), smartposSyncCustomerBranchDTO.getName());
            HttpEntity<SmartposSyncCustomerBranchDTO> entity = new HttpEntity<>(smartposSyncCustomerBranchDTO, getHeaders());
            restTemplate.postForEntity(applicationProperties.getSmartposProperty().getSyncCustomerBranchUrl(), entity, Void.class);
        } catch (Exception e) {
            log.info("Request to Smartpos. TIN: {} Customer branch name: {}", smartposSyncCustomerBranchDTO.getCustomer().getTin(), smartposSyncCustomerBranchDTO.getName());
            throw new AmqpRejectAndDontRequeueException("Sync customer branch on Smartpos error: " + smartposSyncCustomerBranchDTO.getCustomer().getTin());
        }
        log.info(">>>>>>>>>> End Cron Sync Customer Branch To SmartPos  <<<<<<<<<<<<");
    }

    public void bankTariffHistory(BankTariffHistoryDTO dto) {
        log.info("Request to Smart pos bankTariffHistory : {}", dto);
        restTemplate.exchange(
                applicationProperties.getSmartposProperty().getBankTariffHistoryUrl(),
                HttpMethod.POST,
                new HttpEntity<>(dto, getHeaders()),
                Void.class
        );
    }

    public void attachManager(SmartposManagerDTO managerDTO) {
        log.info("Manager DTO: {}", managerDTO);
        restTemplate.exchange(
                applicationProperties.getSmartposProperty().getAttachManager(),
                HttpMethod.PUT,
                new HttpEntity<>(managerDTO, getHeaders()),
                Void.class
        );
    }

    public SmartposLastReceiptResponseDTO getLastReceipt(String deviceSerialNumber, String tin, String paymentType) {
        log.info("Smartpos request to get last receipt. Device serial number: {}, Payment type: {}", deviceSerialNumber, paymentType);
        ResponseEntity<SmartposLastReceiptResponseDTO> response;
        try {
             response = restTemplate.exchange(
                    applicationProperties.getSmartposProperty().getLastReceiptUrl()
                            .concat("?serialNumber=").concat(deviceSerialNumber)
                            .concat("&tin=").concat(tin)
                            .concat("&paymentType=").concat(paymentType),
                    HttpMethod.GET,
                    new HttpEntity<>(getHeaders()),
                    SmartposLastReceiptResponseDTO.class
            );
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw new IntegrationException("An unexpected error occurred while getting last receipt from Smartpos service. Error: " + e.getMessage(), null, HttpStatus.BAD_REQUEST);
        }
        if (HttpStatus.OK == response.getStatusCode()) {
            return response.getBody();
        }
        throw new IntegrationException("An unexpected error occurred while getting last receipt from Smartpos service. Error: " + response, null, HttpStatus.valueOf(response.getStatusCode().value()));
    }

    public void syncCustomer(SmartposFiscalizationDetailDTO customerDTO) {
        restTemplate.exchange(
                applicationProperties.getSmartposProperty().getCustomerSync(),
                HttpMethod.POST,
                new HttpEntity<>(customerDTO, getHeaders()),
                Void.class
        );
    }

    public void removeDevice(String serialNumber) {
        restTemplate.exchange(
                applicationProperties.getSmartposProperty().getRemoveDevice() + "/" + serialNumber,
                HttpMethod.DELETE,
                new HttpEntity<>(getHeaders()),
                Void.class
        );
    }
}
