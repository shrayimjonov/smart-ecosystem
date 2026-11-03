package uz.uzkassa.horeca.integration;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponentsBuilder;
import uz.uzkassa.horeca.dto.auth.AdminLoginDTO;
import uz.uzkassa.horeca.dto.auth.TokenDTO;
import uz.uzkassa.horeca.dto.cts.CtsFiscalCheckDTO;
import uz.uzkassa.horeca.dto.cts.CtsFiscalizationCheckDeviceDTO;
import uz.uzkassa.horeca.dto.soliq.SoliqCompanyDTO;
import uz.uzkassa.horeca.dto.terminal.TxkmResponseDto;
import uz.uzkassa.horeca.utils.HeaderUtils;
import uz.uzkassa.horeca.web.rest.errors.BadRequestException;
import uz.uzkassa.horeca.web.rest.errors.IntegrationException;

import java.time.LocalDateTime;
import java.util.Objects;

@Component
@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class CtsClient extends BaseClient {

    static LocalDateTime authenticateRefreshTime;
    static String tokenStore;

    public String getToken() {
        if (tokenStore != null && authenticateRefreshTime != null && authenticateRefreshTime.isAfter(LocalDateTime.now())) {
            return tokenStore;
        }

        AdminLoginDTO loginDTO = new AdminLoginDTO();
        loginDTO.setPassword(applicationProperties.getCtsConfig().getPassword());
        loginDTO.setUsername(applicationProperties.getCtsConfig().getUsername());
        loginDTO.setRememberMe(true);

        HttpEntity<AdminLoginDTO> entity = new HttpEntity<>(loginDTO, HeaderUtils.getHeader());

        UriComponentsBuilder uriBuilder = UriComponentsBuilder
                .fromUriString(applicationProperties.getCtsConfig().getHost())
                .path(applicationProperties.getCtsConfig().getAuthPath());

        try {
            ResponseEntity<TokenDTO> response = restTemplate.exchange(uriBuilder.toUriString(), HttpMethod.POST, entity, TokenDTO.class);
            if (HttpStatus.OK == response.getStatusCode()) {
                tokenStore = Objects.requireNonNull(response.getBody()).getAccessToken();
                authenticateRefreshTime = LocalDateTime.now().plusSeconds(2592000);
                return tokenStore;
            } else {
                tokenStore = null;
                throw new BadRequestException(response.toString());
            }
        } catch (Exception e) {
            tokenStore = null;
            throw new BadRequestException(e.getMessage());
        }
    }

    public SoliqCompanyDTO getSoliqCompany(String tin) {
        HttpHeaders headers = HeaderUtils.getHeader();
        headers.setBearerAuth(this.getToken());

        UriComponentsBuilder uriBuilder = UriComponentsBuilder
                .fromUriString(applicationProperties.getCtsConfig().getHost())
                .path(applicationProperties.getCtsConfig().getCompanyByTinSoliqPath())
                .path("/")
                .path(tin);
        try {
            ResponseEntity<SoliqCompanyDTO> response = restTemplate.exchange(
                    uriBuilder.toUriString(),
                    HttpMethod.GET,
                    new HttpEntity<>(headers),
                    SoliqCompanyDTO.class
            );
            if (HttpStatus.OK == response.getStatusCode()) {
                return response.getBody();
            } else {
                throw new IntegrationException(response.toString());
            }
        } catch (Exception e) {
            throw new IntegrationException("Check company from CTS error", e.getMessage());
        }
    }

    public CtsFiscalizationCheckDeviceDTO fiscalCheck(CtsFiscalCheckDTO ctsFiscalCheckDTO) {
        HttpHeaders headers = HeaderUtils.getHeader();
        headers.setBearerAuth(this.getToken());
        HttpEntity<?> entity = new HttpEntity<>(ctsFiscalCheckDTO, headers);

        UriComponentsBuilder uriBuilder = UriComponentsBuilder
                .fromUriString(applicationProperties.getCtsConfig().getHost())
                .path(applicationProperties.getCtsConfig().getFiscalCheckPath());

        try {
            ResponseEntity<CtsFiscalizationCheckDeviceDTO> response = restTemplate.exchange(
                    uriBuilder.toUriString(),
                    HttpMethod.POST,
                    entity,
                    CtsFiscalizationCheckDeviceDTO.class
            );
            if (HttpStatus.OK == response.getStatusCode()) {
                return response.getBody();
            } else {
                throw new IntegrationException(response.toString());
            }
        } catch (Exception e) {
            throw new IntegrationException("Fiscal check from Cts error", e.getMessage());
        }
    }

    public TxkmResponseDto getTxkmInfoByDevice(final Integer pageNo,
                                               final Integer pageSize,
                                               String serialNumber) {
        HttpHeaders headers = HeaderUtils.getHeader();
        headers.setBearerAuth(this.getToken());
        HttpEntity<?> entity = new HttpEntity<>(headers);

        UriComponentsBuilder uriBuilder = UriComponentsBuilder
                .fromUriString(applicationProperties.getCtsConfig().getHost())
                .path("/api/smartpos/txkm-device")
                .queryParam("pageNo", pageNo)
                .queryParam("pageSize", pageSize)
                .queryParam("serialNumber", serialNumber);

        try {
            ResponseEntity<TxkmResponseDto> response = restTemplate.exchange(
                    uriBuilder.toUriString(),
                    HttpMethod.GET,
                    entity,
                    TxkmResponseDto.class
            );
            if (HttpStatus.OK == response.getStatusCode()) {
                return response.getBody();
            } else {
                throw new IntegrationException(response.toString());
            }
        } catch (Exception e) {
            throw new IntegrationException("GET Txkm Info By Device", e.getMessage());
        }
    }
}
