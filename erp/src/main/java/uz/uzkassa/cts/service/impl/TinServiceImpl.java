package uz.uzkassa.cts.service.impl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.http.client.support.BasicAuthenticationInterceptor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;
import uz.uzkassa.cts.config.ApplicationProperties;
import uz.uzkassa.cts.constants.MessageConstants;
import uz.uzkassa.cts.domain.Company;
import uz.uzkassa.cts.domain.Customer;
import uz.uzkassa.cts.domain.Fiscalization;
import uz.uzkassa.cts.dto.tin.TaxRequest;
import uz.uzkassa.cts.dto.tin.TaxResponseDTO;
import uz.uzkassa.cts.dto.tin.TinDTO;
import uz.uzkassa.cts.dto.tin.integration.*;
import uz.uzkassa.cts.integrations.OfdClient;
import uz.uzkassa.cts.repository.CompanyRepository;
import uz.uzkassa.cts.repository.CustomerRepository;
import uz.uzkassa.cts.repository.FiscalizationRepository;
import uz.uzkassa.cts.security.SecurityUtils;
import uz.uzkassa.cts.service.TinService;
import uz.uzkassa.cts.service.ValidationService;
import uz.uzkassa.cts.utils.Utils;
import uz.uzkassa.cts.validator.LocalizationMessage;
import uz.uzkassa.cts.web.rest.errors.ErrorDTO;
import uz.uzkassa.cts.web.rest.errors.IntegrationException;

import java.math.BigDecimal;
import java.util.List;

@Transactional
@Service
@Slf4j
public class TinServiceImpl implements TinService, MessageConstants {

    private final CompanyRepository companyRepository;
    private final CustomerRepository customerRepository;
    private final OfdClient ofdClient;
    private final ApplicationProperties applicationProperties;
    private final FiscalizationRepository fiscalizationRepository;
    private final ValidationService validationService;
    private final LocalizationMessage localizationMessage;


    @Value("${taxcredential.url}")
    String taxUrl;

    @Value("${taxcredential.username}")
    String taxUsername;

    @Value("${taxcredential.password}")
    String taxPassword;

    @Value("${spring.profiles.active}")
    String activeProfile;

    @Autowired
    private RestTemplate restTemplate;

    public TinServiceImpl(CompanyRepository companyRepository,
                          CustomerRepository customerRepository,
                          OfdClient ofdClient, ApplicationProperties applicationProperties,
                          FiscalizationRepository fiscalizationRepository, ValidationService validationService, LocalizationMessage localizationMessage) {
        this.companyRepository = companyRepository;
        this.validationService = validationService;
        this.customerRepository = customerRepository;
        this.ofdClient = ofdClient;
        this.applicationProperties = applicationProperties;
        this.fiscalizationRepository = fiscalizationRepository;
        this.localizationMessage = localizationMessage;
    }

    @Override
    @Transactional(readOnly = true)
    public TinDTO getCompanyByTin(String tin) {
        Company company = companyRepository.findByTin(tin);
        if (company != null) {
            throw validationService.entityAlreadyExistExceptionThrow(COMPANY, TIN, company.getTin()).get();
        }
        return getTinInfo(tin);
    }

    @Override
    @Transactional(readOnly = true)
    public TinDTO getCustomerByTin(String tin) {
        Customer customer = customerRepository.findByTin(tin);
        if (customer != null) {
            throw validationService.entityAlreadyExistExceptionThrow(CUSTOMER, TIN, customer.getTin()).get();
        }
        return getTinInfo(tin);
    }

    @Override
    @Transactional(readOnly = true)
    public OfdCustomerBalanceResponseDTO getCustomerBalance(String login, String tin) {
        log.info(">>>>>>>>>> Start Get Customer Balance <<<<<<<<<<<<");
        if ("DEV".equalsIgnoreCase(activeProfile) || customerRepository.findByTin(tin).isWhiteList()) {
            OfdAccountDTO ofdAccountDTO = new OfdAccountDTO();
            ofdAccountDTO.setTin(tin);
            ofdAccountDTO.setOverpaymentSum(new BigDecimal(600));
            ofdAccountDTO.setUnpaidCount(0);
            ofdAccountDTO.setTerminalCost(new BigDecimal(300));

            OfdCustomerBalanceResponseDTO responseBodyDTO = new OfdCustomerBalanceResponseDTO();
            responseBodyDTO.setSuccess(true);
            responseBodyDTO.setData(ofdAccountDTO);
            responseBodyDTO.setCode(1);
            responseBodyDTO.setText("On dev environment, we don't really check client balance. This is mock data.");
            log.info(">>>>>>>>>> End Get Customer Balance <<<<<<<<<<<<");
            return responseBodyDTO;
        }

        try {

            String checkBalanceUrl;
            if (Utils.isTin(tin)) {
                checkBalanceUrl = applicationProperties.getOfdProperty().getCustomerBalanceCheckUrl().concat("?tin=").concat(tin);
            } else {
                checkBalanceUrl = applicationProperties.getOfdProperty().getCustomerBalanceCheckUrl().concat("?pinfl=").concat(tin);
            }

            HttpHeaders headers = SecurityUtils.getHeader();
            headers.setBearerAuth(ofdClient.getToken());

            HttpEntity<OfdCustomerBalanceResponseDTO> request = new HttpEntity<>(headers);
            ResponseEntity<OfdCustomerBalanceResponseDTO> response = restTemplate.exchange(checkBalanceUrl, HttpMethod.GET, request, OfdCustomerBalanceResponseDTO.class);
            OfdCustomerBalanceResponseDTO responseBodyDTO = response.getBody();
            OfdAccountDTO ofdAccountDTO = response.getBody() != null ? response.getBody().getData() : null;

            if (ofdAccountDTO == null) {
                ofdAccountDTO = new OfdAccountDTO();
                ofdAccountDTO.setUnpaidCount(0);
                responseBodyDTO.setData(ofdAccountDTO);
                responseBodyDTO.setSuccess(false);
                responseBodyDTO.setText(responseBodyDTO.getText());
                log.info(">>>>>>>>>> End Get Customer Balance <<<<<<<<<<<<");
                return responseBodyDTO;
            }

            List<Fiscalization> fiscalizations = fiscalizationRepository.findByCustomerTin(tin);
            BigDecimal needAmount = !fiscalizations.isEmpty() ? ofdAccountDTO.getTerminalCost().multiply(new BigDecimal(fiscalizations.size())) : BigDecimal.ZERO;

            BigDecimal ofdBalance = ofdAccountDTO.getOverpaymentSum();
            BigDecimal differenceAmount = ofdBalance.subtract(needAmount);

            if (differenceAmount.compareTo(ofdAccountDTO.getTerminalCost()) >= 0) {

                return response.getBody();
            }

            ofdAccountDTO.setUnpaidCount(0);
            responseBodyDTO.setData(ofdAccountDTO);
            responseBodyDTO.setSuccess(false);
            responseBodyDTO.setText(localizationMessage.localize("ofd.balance.check.info", new Object[]{ofdAccountDTO.getTerminalCost(), ofdBalance, ofdAccountDTO.getTerminalCost(), ofdAccountDTO.getUnpaidCount()}));
            log.info(">>>>>>>>>> End Get Customer Balance <<<<<<<<<<<<");
            return responseBodyDTO;
        } catch (Exception e) {
            if (e instanceof IntegrationException) {
                ErrorDTO errorDTO = ((IntegrationException) e).getError();
                if (HttpStatus.UNAUTHORIZED.value() == errorDTO.getStatus()) {
                    errorDTO.setStatus(HttpStatus.UNAVAILABLE_FOR_LEGAL_REASONS.value());
                    errorDTO.setError(localizationMessage.localize("no.connection.with.nic"));
                    errorDTO.setMessage(localizationMessage.localize("no.connection.with.nic"));
                    throw new IntegrationException(localizationMessage.localize("no.connection.with.nic"), errorDTO);
                }
                throw new IntegrationException(localizationMessage.localize("no.connection.with.nic"), errorDTO);
            }
            throw new IntegrationException(localizationMessage.localize("check.tin.empty.data", new Object[]{tin, e.getMessage()}));
        }
    }

    @Override
    public OfdTokenResponseDTO getToken(String tin) {
        try {
            String url = applicationProperties.getOfdProperty().getTokenUrl().concat(tin);
            HttpHeaders headers = SecurityUtils.getHeader();

            HttpEntity<OfdTokenResponseDTO> request = new HttpEntity<>(headers);
            ResponseEntity<OfdTokenResponseDTO> response = restTemplate.exchange(url, HttpMethod.GET, request, OfdTokenResponseDTO.class);
            return response.getBody();
        } catch (Exception e) {
            if (e instanceof IntegrationException) {
                ErrorDTO errorDTO = ((IntegrationException) e).getError();
                if (HttpStatus.UNAUTHORIZED.value() == errorDTO.getStatus()) {
                    errorDTO.setError(localizationMessage.localize("no.connection.with.nic"));
                    errorDTO.setMessage(localizationMessage.localize("no.connection.with.nic"));
                    throw new IntegrationException(localizationMessage.localize("no.connection.with.nic"), errorDTO);
                }
                throw new IntegrationException(e.getMessage(), errorDTO);
            }
            throw new IntegrationException(localizationMessage.localize(UNEXPECTED_ERROR, new Object[]{e.getMessage()}));
        }
    }

    @Override
    public OfdResponseDTO verify(SignedFormDto signedFormDto) {
        try {
            HttpHeaders headers = SecurityUtils.getHeader();
            HttpEntity<SignedFormDto> entity = new HttpEntity<>(signedFormDto, headers);
            ResponseEntity<OfdResponseDTO> response = restTemplate.exchange(applicationProperties.getOfdProperty().getVerifyUrl(), HttpMethod.POST, entity, OfdResponseDTO.class);
            return response.getBody();
        } catch (Exception e) {
            if (e instanceof IntegrationException) {
                ErrorDTO errorDTO = ((IntegrationException) e).getError();
                if (HttpStatus.UNAUTHORIZED.value() == errorDTO.getStatus()) {
                    errorDTO.setStatus(HttpStatus.UNAVAILABLE_FOR_LEGAL_REASONS.value());
                    errorDTO.setError(localizationMessage.localize("no.connection.with.nic"));
                    errorDTO.setMessage(localizationMessage.localize("no.connection.with.nic"));
                    throw new IntegrationException(localizationMessage.localize("no.connection.with.nic"), errorDTO);
                }
                throw new IntegrationException(e.getMessage(), errorDTO);
            }
            throw new IntegrationException(localizationMessage.localize(UNEXPECTED_ERROR, new Object[]{e.getMessage()}));
        }
    }

    private TinDTO getTinInfo(String tin) {
        TaxRequest taxRequest = new TaxRequest();
        taxRequest.setAction("recvisits");
        taxRequest.setTin(tin);
        restTemplate.getInterceptors().add(new BasicAuthenticationInterceptor(taxUsername, taxPassword));
        ResponseEntity<TaxResponseDTO> responseEntity = null;
        try {
            responseEntity = restTemplate.postForEntity(taxUrl, taxRequest, TaxResponseDTO.class);
        } catch (Exception e) {
            log.error("Error occurred while requesting soliq.uz", e);
        }
        if (responseEntity == null) {
            return null;
        }
        if (responseEntity.getBody() != null) {

            log.info("Tax Response : {}", responseEntity.getBody().toString());

            TaxResponseDTO resTemplate = responseEntity.getBody();

            if (resTemplate != null && resTemplate.getErr_code() == 0 && resTemplate.getRoot() != null && !resTemplate.getRoot().isEmpty()) {
                TinDTO tinDTO = new TinDTO();
                tinDTO.setTin(tin);
                tinDTO.setName(resTemplate.getRoot().getFirst().getAname());
                tinDTO.setAddress(resTemplate.getRoot().getFirst().getAddress());
                return tinDTO;
            }
        } else {
            log.info("Response status: {}", responseEntity.getStatusCode());
            return null;
        }
        return null;
    }

}
