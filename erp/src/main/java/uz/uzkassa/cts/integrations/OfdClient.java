package uz.uzkassa.cts.integrations;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Lazy;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import uz.uzkassa.cts.dto.auth.AuthenticateDTO;
import uz.uzkassa.cts.dto.ofd.OfdAuthLoginResultDTO;
import uz.uzkassa.cts.dto.ofd.OfdCustomerBranchAddUpdateDTO;
import uz.uzkassa.cts.dto.ofd.OfdCustomerFiscalDTO;
import uz.uzkassa.cts.dto.payload.OfdApplicationPayload;
import uz.uzkassa.cts.dto.tin.integration.*;
import uz.uzkassa.cts.enums.IntegrationType;
import uz.uzkassa.cts.security.SecurityUtils;
import uz.uzkassa.cts.utils.Utils;
import uz.uzkassa.cts.web.rest.errors.IntegrationException;

@Component
@Slf4j
@Lazy
public class OfdClient extends BaseClient {

    @Override
    public String authenticate() {
        AuthenticateDTO authenticateDTO = new AuthenticateDTO();
        authenticateDTO.setUsername(applicationProperties.getOfdProperty().getUsername());
        authenticateDTO.setPassword(applicationProperties.getOfdProperty().getPassword());
        HttpEntity<AuthenticateDTO> entity = new HttpEntity<>(authenticateDTO, SecurityUtils.getHeader());
        ResponseEntity<OfdAuthLoginResultDTO> response;
        try {
            response = restTemplate.exchange(
                    applicationProperties.getOfdProperty().getAuthLoginUrl(),
                    HttpMethod.POST,
                    entity,
                    OfdAuthLoginResultDTO.class
            );
            log.info("Authenticate OFD response :{}", response);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw new IntegrationException("An unexpected error has occurred while authenticating to OFD service. Error:" + e.getMessage(), null, HttpStatus.BAD_REQUEST);
        }
        if (response.getBody() != null && response.getBody().getAccess_token() != null) {
            log.info("Record OFD token to redis :{}", response.getBody().getAccess_token());
            tokenStoreService.create(getIntegrationType(), response.getBody().getAccess_token(), Long.valueOf(response.getBody().getExpire()) - 300L);
            return response.getBody().getAccess_token();
        }
        throw new IntegrationException("An unexpected error has occurred while authenticating to OFD service. Error:" + response, null, HttpStatus.valueOf(response.getStatusCode().value()));
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
        headers.setBearerAuth(this.getToken());
        return headers;
    }

    @Override
    public IntegrationType getIntegrationType() {
        return IntegrationType.OFD;
    }

    public OfdInactiveFiscalModuleResponseDTO getInactiveFiscalModules() {
        HttpEntity<OfdInactiveFiscalModuleResponseDTO> request = new HttpEntity<>(getHeaders());
        try {
            ResponseEntity<OfdInactiveFiscalModuleResponseDTO> response = restTemplate.exchange(applicationProperties.getOfdProperty().getInactiveFiscalModuleListUrl().concat("?page=1&size=10000000"), HttpMethod.GET, request, OfdInactiveFiscalModuleResponseDTO.class);
            return response.getBody();
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw new IntegrationException("An unexpected error has occurred while getting inactive fiscal modules from OFD service. Error:" + e.getMessage(), null, HttpStatus.BAD_REQUEST);
        }
    }

    public OfdRegionListResponseDTO getRegions(String language) {
        HttpEntity<OfdApplicationListResponseDTO> request = new HttpEntity<>(getHeaders());
        String url = applicationProperties.getOfdProperty().getRegionListUrl();
        url = url.concat("?lang=" + language);
        ResponseEntity<OfdRegionListResponseDTO> response = restTemplate.exchange(url, HttpMethod.GET, request, OfdRegionListResponseDTO.class);
        return response.getBody();
    }

    public OfdRegionListResponseDTO getDistricts(Integer regionCode, String language) {
        HttpEntity<OfdApplicationListResponseDTO> request = new HttpEntity<>(getHeaders());
        String url = applicationProperties.getOfdProperty().getDistrictListUrl();
        url = url.concat("?region_code=" + regionCode);
        url = url.concat("&lang=" + language);
        ResponseEntity<OfdRegionListResponseDTO> response = restTemplate.exchange(url, HttpMethod.GET, request, OfdRegionListResponseDTO.class);
        return response.getBody();
    }

    public OfdNeighborhoodListResponseDTO getNeighborhoods(Long regionCode, Long districtCode) {
        HttpEntity<OfdApplicationListResponseDTO> request = new HttpEntity<>(getHeaders());
        String url = applicationProperties.getOfdProperty().getNeighborhoodListUrl();
        url = url.concat("?region_code=" + regionCode);
        url = url.concat("&district_code=" + districtCode);
        ResponseEntity<OfdNeighborhoodListResponseDTO> response = restTemplate.exchange(url, HttpMethod.GET, request, OfdNeighborhoodListResponseDTO.class);
        return response.getBody();
    }

    public OfdActivityGroupListResponseDTO getActivityTypeGroups() {
        HttpEntity<OfdApplicationListResponseDTO> request = new HttpEntity<>(getHeaders());
        ResponseEntity<OfdActivityGroupListResponseDTO> response = restTemplate.exchange(applicationProperties.getOfdProperty().getActivityGroupUrl(), HttpMethod.GET, request, OfdActivityGroupListResponseDTO.class);
        return response.getBody();
    }

    public OfdActivityGroupListResponseDTO getActivityTypes(Integer groupCode) {
        HttpEntity<OfdApplicationListResponseDTO> request = new HttpEntity<>(getHeaders());
        String url = applicationProperties.getOfdProperty().getActivityListUrl();
        url = url.concat("?group_code=" + groupCode);
        ResponseEntity<OfdActivityGroupListResponseDTO> response = restTemplate.exchange(url, HttpMethod.GET, request, OfdActivityGroupListResponseDTO.class);
        return response.getBody();
    }

    /*public TxkmResponseDTO getTxkmByFmSerial(TxkmDTO txkmDTO) {
        HttpHeaders headers = SecurityUtils.getHeader();
        headers.setBasicAuth(txkmDTO.getTin(), txkmDTO.getTin());
        String url = applicationProperties.getTxkmProperty().getHost();
        url = url.concat("/information/get-notification");
        url = url.concat("?pageNo=" + txkmDTO.getPageNo());
        url = url.concat("&pageSize=" + txkmDTO.getPageSize());
        url = url.concat("&terminalId=").concat(txkmDTO.getTerminalId());
        ResponseEntity<TxkmResponseDTO> response = restTemplate.exchange(url, HttpMethod.GET, new HttpEntity<>(headers), TxkmResponseDTO.class);
        return response.getBody();
    }*/

    public String getScannerListString() {
        HttpEntity<?> request = new HttpEntity<>(getHeaders());
        ResponseEntity<String> response = restTemplate.exchange(applicationProperties.getOfdProperty().getScannerUrl(), HttpMethod.GET, request, String.class);
        return response.getBody();
    }

    public OfdScannerGroupListResponseDTO getScannerList() {
        HttpEntity<OfdApplicationListResponseDTO> request = new HttpEntity<>(getHeaders());
        ResponseEntity<OfdScannerGroupListResponseDTO> response =
                restTemplate.exchange(applicationProperties.getOfdProperty().getScannerUrl(), HttpMethod.GET, request, OfdScannerGroupListResponseDTO.class);
        return response.getBody();
    }

    public String getTerminalTypes() {
        ResponseEntity<String> response = restTemplate.exchange(applicationProperties.getOfdProperty().getCategoryListUrl(), HttpMethod.GET, new HttpEntity<>(getHeaders()), String.class);
        return response.getBody();
    }

    public String getInactiveFiscalModulesString() {
        HttpEntity<OfdInactiveFiscalModuleResponseDTO> request = new HttpEntity<>(getHeaders());
        ResponseEntity<String> response = restTemplate.exchange(applicationProperties.getOfdProperty().getInactiveFiscalModuleListUrl().concat("?page=1&size=10000000"), HttpMethod.GET, request, String.class);
        return response.getBody();
    }

    public OfdAccountDTO checkBalance(String tin) {
        String url;
        if (Utils.isTin(tin)) {
            url = applicationProperties.getOfdProperty().getCustomerBalanceCheckUrl().concat("?tin=").concat(tin);
        } else {
            url = applicationProperties.getOfdProperty().getCustomerBalanceCheckUrl().concat("?pinfl=").concat(tin);
        }
        HttpEntity<OfdCustomerBalanceResponseDTO> checkBalanceRequest = new HttpEntity<>(getHeaders());
        try {
            ResponseEntity<OfdCustomerBalanceResponseDTO> response = restTemplate.exchange(url, HttpMethod.GET, checkBalanceRequest, OfdCustomerBalanceResponseDTO.class);
            if (response.getBody() != null && response.getBody().isSuccess() && response.getBody().getData() != null) {
                return response.getBody().getData();
            }
            throw new IntegrationException("An unexpected error has occurred while getting company balance from OFD service. Error:" + response, null, HttpStatus.valueOf(response.getStatusCode().value()));
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw new IntegrationException("An unexpected error has occurred while getting company balance from OFD service. Error:" + e.getMessage(), null, HttpStatus.BAD_REQUEST);
        }
    }

    public OfdApplicationSendResponseDTO makeCustomerFiscal(OfdCustomerFiscalDTO ofdCustomerFiscalDTO) {
        try {
            ResponseEntity<OfdApplicationSendResponseDTO> response = restTemplate.exchange(applicationProperties.getOfdProperty().getCustomerFiscalAddUrl(), HttpMethod.POST, new HttpEntity<>(ofdCustomerFiscalDTO, getHeaders()), OfdApplicationSendResponseDTO.class);
            return response.getBody();
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw new IntegrationException("An unexpected error has occurred while making company fiscal to OFD service. Error:" + e.getMessage(), null, HttpStatus.BAD_REQUEST);
        }
    }

    public OfdGenerateApplicationFileResponseDTO generateApplicationFile(OfdGenerateApplicationFileRequestDTO fileGenerateRequestDTO) {
        String apiUrl = applicationProperties.getOfdProperty().getGenerateApplicationFileUrl() +
                "?officeId=" + fileGenerateRequestDTO.getOfficeId() +
                "&ccmSerialNumber=" + fileGenerateRequestDTO.getCcmSerialNumber() +
                "&ccmCategoryId=" + fileGenerateRequestDTO.getCcmCategoryId() +
                "&terminalId=" + fileGenerateRequestDTO.getTerminalId() +
                "&phone=" + fileGenerateRequestDTO.getPhone();

        HttpEntity<OfdGenerateApplicationFileRequestDTO> entity = new HttpEntity<>(fileGenerateRequestDTO, getHeaders());
        ResponseEntity<OfdGenerateApplicationFileResponseDTO> response = restTemplate.exchange(apiUrl, HttpMethod.GET, entity, OfdGenerateApplicationFileResponseDTO.class);
        return response.getBody();
    }

    public OfdCustomerBranchResponseDTO createOrUpdateCustomerBranch(Long ofdId, OfdCustomerBranchAddUpdateDTO customerBranchAddUpdateDTO) {
        String url = ofdId == null ? applicationProperties.getOfdProperty().getCustomerBranchAddUrl() : applicationProperties.getOfdProperty().getCustomerBranchUpdateUrl();
        HttpEntity<OfdCustomerBranchAddUpdateDTO> entity = new HttpEntity<>(customerBranchAddUpdateDTO, getHeaders());
        ResponseEntity<OfdCustomerBranchResponseDTO> response = ofdId == null ?
                restTemplate.exchange(url, HttpMethod.POST, entity, OfdCustomerBranchResponseDTO.class) :
                restTemplate.exchange(url, HttpMethod.PUT, entity, OfdCustomerBranchResponseDTO.class);
        return response.getBody();
    }

    public OfdApplicationListResponseDTO batchSyncApplication(Long ofdId) {
        String url = applicationProperties.getOfdProperty().getCustomerFiscalListUrl().concat("?id=").concat(String.valueOf(ofdId));
        HttpEntity<OfdApplicationListResponseDTO> request = new HttpEntity<>(getHeaders());
        ResponseEntity<OfdApplicationListResponseDTO> response = restTemplate.exchange(url, HttpMethod.GET, request, OfdApplicationListResponseDTO.class);
        return response.getBody();
    }

    public OfdApplicationListResponseDTO syncApplication(OfdApplicationPayload ofdApplicationPayload) {
        HttpEntity<OfdApplicationListResponseDTO> request = new HttpEntity<>(getHeaders());
        ResponseEntity<OfdApplicationListResponseDTO> response = restTemplate.exchange(ofdApplicationPayload.getApiUrl(), HttpMethod.GET, request, OfdApplicationListResponseDTO.class);
        return response.getBody();
    }

    public OfdApplicationSendResponseDTO sendApplication(OfdCustomerFiscalDTO ofdCustomerFiscalDTO) {
        HttpEntity<OfdCustomerFiscalDTO> entity = new HttpEntity<>(ofdCustomerFiscalDTO, getHeaders());
        ResponseEntity<OfdApplicationSendResponseDTO> response = restTemplate.exchange(applicationProperties.getOfdProperty().getCustomerFiscalAddUrl(), HttpMethod.POST, entity, OfdApplicationSendResponseDTO.class);
        return response.getBody();
    }

    public OfdApplicationListResponseDTO getApplicationById(Long ofdId) {
        HttpEntity<OfdApplicationListResponseDTO> request = new HttpEntity<>(getHeaders());
        ResponseEntity<OfdApplicationListResponseDTO> response = restTemplate.exchange(applicationProperties.getOfdProperty().getCustomerFiscalListUrl().concat("?id=" + ofdId), HttpMethod.GET, request, OfdApplicationListResponseDTO.class);
        return response.getBody();
    }

    public OfdApplicationListResponseDTO getApplicationListByTin(String tin) {
        String baseUrl = applicationProperties.getOfdProperty().getCustomerFiscalListUrl().concat("?page=1&size=1000");
        String apiUrl;
        if (Utils.isTin(tin)) {
            apiUrl = baseUrl.concat("&tin=").concat(tin);
        } else {
            apiUrl = baseUrl.concat("&pinfl=").concat(tin);
        }

        HttpEntity<OfdApplicationListResponseDTO> request = new HttpEntity<>(getHeaders());
        try {
            ResponseEntity<OfdApplicationListResponseDTO> response = restTemplate.exchange(apiUrl, HttpMethod.GET, request, OfdApplicationListResponseDTO.class);
            return response.getBody();
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw new IntegrationException("An unexpected error has occurred while getting application list from OFD service. Error:" + e.getMessage(), null, HttpStatus.BAD_REQUEST);
        }
    }

    public OfdApplicationRejectResponseDTO rejectApplication(OfdApplicationRejectDTO ofdApplicationRejectDTO) {
        HttpEntity<OfdApplicationRejectDTO> entity = new HttpEntity<>(ofdApplicationRejectDTO, getHeaders());
        try {
            ResponseEntity<OfdApplicationRejectResponseDTO> response = restTemplate.exchange(applicationProperties.getOfdProperty().getRejectApplicationUrl(), HttpMethod.POST, entity, OfdApplicationRejectResponseDTO.class);
            return response.getBody();
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw new IntegrationException("An unexpected error has occurred while rejecting application to OFD service. Error:" + e.getMessage(), null, HttpStatus.BAD_REQUEST);
        }
    }

    public OfdSwapFiscalModuleResponseDTO swapFiscalModule(OfdSwapFiscalModuleRequestDTO ofdSwapFiscalModuleRequestDTO) {
        HttpEntity<OfdSwapFiscalModuleRequestDTO> entity = new HttpEntity<>(ofdSwapFiscalModuleRequestDTO, getHeaders());
        try {
            ResponseEntity<OfdSwapFiscalModuleResponseDTO> response = restTemplate.exchange(applicationProperties.getOfdProperty().getChangeFmCcmUrl(), HttpMethod.POST, entity, OfdSwapFiscalModuleResponseDTO.class);
            return response.getBody();
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw new IntegrationException("An unexpected error has occurred while swapping fiscal module to OFD service. Error:" + e.getMessage(), null, HttpStatus.BAD_REQUEST);
        }
    }

    public OfdSwapDeviceResponseDTO swapDevice(OfdSwapDeviceRequestDTO ofdSwapDeviceRequestDTO) {
        HttpEntity<OfdSwapDeviceRequestDTO> entity = new HttpEntity<>(ofdSwapDeviceRequestDTO, getHeaders());
        try {
            ResponseEntity<OfdSwapDeviceResponseDTO> response = restTemplate.exchange(applicationProperties.getOfdProperty().getChangeFmCcmUrl(), HttpMethod.POST, entity, OfdSwapDeviceResponseDTO.class);
            return response.getBody();
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw new IntegrationException("An unexpected error has occurred while swapping terminal to OFD service. Error:" + e.getMessage(), null, HttpStatus.BAD_REQUEST);
        }
    }

    public OfdChangeAddressResponseDTO changeAddress(OfdChangeAddressRequestDTO ofdChangeAddressRequestDTO) {
        HttpEntity<OfdChangeAddressRequestDTO> entity = new HttpEntity<>(ofdChangeAddressRequestDTO, getHeaders());
        try {
            ResponseEntity<OfdChangeAddressResponseDTO> response = restTemplate.exchange(applicationProperties.getOfdProperty().getChangeAddressUrl(), HttpMethod.POST, entity, OfdChangeAddressResponseDTO.class);
            return response.getBody();
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw new IntegrationException("An unexpected error has occurred while changing address to OFD service. Error:" + e.getMessage(), null, HttpStatus.BAD_REQUEST);
        }
    }
}
