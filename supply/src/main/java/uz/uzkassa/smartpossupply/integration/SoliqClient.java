package uz.uzkassa.smartpossupply.integration;

import org.springframework.util.StringUtils;
import uz.uzkassa.smartpossupply.constants.MessageConstants;

import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.client.circuitbreaker.CircuitBreaker;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import uz.uzkassa.smartpossupply.dto.factura.CompanyCheckDTO;
import uz.uzkassa.smartpossupply.dto.soliq.TaxCompanyBranchDTO;
import uz.uzkassa.smartpossupply.dto.soliq.TaxCompanyDTO;
import uz.uzkassa.smartpossupply.dto.soliq.VATCompanyDateDTO;
import uz.uzkassa.smartpossupply.repository.FacturaHostRepository;
import uz.uzkassa.smartpossupply.utils.DateUtils;
import uz.uzkassa.smartpossupply.utils.SecurityUtils;
import uz.uzkassa.smartpossupply.web.rest.errors.IntegrationException;

import java.time.LocalDate;
import java.util.Optional;

import static lombok.AccessLevel.PRIVATE;

@Component
@Slf4j
@FieldDefaults(level = PRIVATE, makeFinal = true)
@RequiredArgsConstructor
public class SoliqClient extends BaseClient {

    private final FacturaHostRepository facturaHostRepository;

    public IntegrationType getIntegrationType() {
        return IntegrationType.SOLIQ;
    }

    private String getFacturaHost() {
        if (facturaHostRepository.findFirstByActiveIsTrue().isPresent()) {
            return facturaHostRepository.findFirstByActiveIsTrue().get().getHost();
        }
        return applicationProperties.getFacturaConfig().getUrl();
    }

    public CompanyCheckDTO checkTin(String tin) {
        CircuitBreaker cb = circuitBreakerFactory.create(getIntegrationType().name());
        return cb.run(
                () -> {
                    StringBuilder url = new StringBuilder();
                    url.append(this.getFacturaHost());
                    url.append("/mysoliq/services");
                    url.append("/np1/bytin/factura-all?");
                    if (tin.length() > 9) {
                        url.append("pinfl=");
                    } else {
                        url.append("tin=");
                    }
                    url.append(tin);

                    try {
                        log.info("Check TIN from Soliq request: {}", buildCurl(url.toString()));
                        ResponseEntity<String> response = restTemplate.exchange(
                                url.toString(),
                                HttpMethod.GET,
                                new HttpEntity<>(getHeaders()),
                                String.class
                        );

                        log.info("Check TIN from Soliq response: {}", response);
                        if (HttpStatus.OK == response.getStatusCode()) {
                            CompanyCheckDTO result =
                                    objectMapper.readValue(response.getBody(), CompanyCheckDTO.class);

                            if (tin.length() > 9 && !StringUtils.hasText(result.getDirector())) {
                                result.setDirector(result.getName());
                            }

                            return result;
                        } else {
                            log.error("Check TIN from Soliq response: {}", response);
                            throw new IntegrationException(
                                    localizationService.localize(MessageConstants.SOLIQ_CHECK_TIN_ERROR),
                                    response.getBody(),
                                    response.getStatusCode()
                            );
                        }
                    } catch (Exception e) {
                        log.error("Json parse error: {}", e.getMessage());
                        throw new IntegrationException(
                                localizationService.localize(
                                        MessageConstants.SOLIQ_CHECK_TIN_ERROR,
                                        new Object[]{tin}
                                ),
                                e.getMessage()
                        );
                    }
                },
                throwable -> {
                    log.error(throwable.getMessage(), throwable);
                    throw new IntegrationException(throwable.getMessage());
                }
        );
    }

    public TaxCompanyDTO getCustomerByTin(String tin) {
        return tin.startsWith("3") || tin.startsWith("2") ? getCompanyByTin(tin) : getUserByTin(tin);
    }

    private TaxCompanyDTO getCompanyByTin(String tin) {
        CircuitBreaker cb = circuitBreakerFactory.create(getIntegrationType().name());
        return cb.run(
                () -> {
                    StringBuilder url = new StringBuilder();
                    url.append(this.getFacturaHost());
                    url.append("/mysoliq/services");
                    url.append("/np1/bytin/factura?lang=ru&tin=").append(tin);

                    try {
                        ResponseEntity<String> response = restTemplate.exchange(url.toString(), HttpMethod.GET, new HttpEntity<>(getHeaders()), String.class);
                        if (HttpStatus.OK == response.getStatusCode()) {
                            return objectMapper.readValue(response.getBody(), TaxCompanyDTO.class);
                        } else {
                            log.error("Get company by tin from Soliq response: {}", response);
                            throw new IntegrationException(localizationService.localize(MessageConstants.GET_COMPANY_TIN_ERROR), response.getBody(), response.getStatusCode());
                        }
                    } catch (Exception e) {
                        log.error("Json parse error: {}", e.getMessage());
                        throw new IntegrationException(localizationService.localize(MessageConstants.GET_COMPANY_TIN_ERROR), e.getMessage());
                    }
                },
                throwable -> {
                    log.error(throwable.getMessage(), throwable);
                    throw new IntegrationException(throwable.getMessage());
                }
        );
    }

    public TaxCompanyDTO getUserByTin(String tin) {
        CircuitBreaker cb = circuitBreakerFactory.create(getIntegrationType().name());
        return cb.run(
                () -> {
                    StringBuilder url = new StringBuilder();
                    url.append(this.getFacturaHost());
                    url.append("/mysoliq/services");
                    url.append("/np1/phisbytin/factura?lang=ru&tin=").append(tin);
                    try {
                        ResponseEntity<String> response = restTemplate.exchange(url.toString(), HttpMethod.GET, new HttpEntity<>(getHeaders()), String.class);
                        if (HttpStatus.OK == response.getStatusCode()) {
                            return objectMapper.readValue(response.getBody(), TaxCompanyDTO.class);
                        } else {
                            log.error("Get company by tin from Soliq response: {}", response);
                            throw new IntegrationException(localizationService.localize(MessageConstants.GET_USER_TIN_ERROR), response.getBody(), response.getStatusCode());
                        }
                    } catch (Exception e) {
                        log.error("Json parse error: {}", e.getMessage());
                        throw new IntegrationException(localizationService.localize(MessageConstants.GET_USER_TIN_ERROR), e.getMessage());
                    }
                },
                throwable -> {
                    log.error(throwable.getMessage(), throwable);
                    throw new IntegrationException(throwable.getMessage());
                }
        );
    }

    public VATCompanyDateDTO getCompanyVatByDate(String tin, LocalDate date, boolean throwException) {
        CircuitBreaker cb = circuitBreakerFactory.create(getIntegrationType().name());
        return cb.run(
                () -> {
                    StringBuilder url = new StringBuilder();
                    url.append(this.getFacturaHost());
                    url.append("/mysoliq/services");
                    url.append("/nds/reference/status?");
                    if (tin.length() > 9) {
                        url.append("pinfl=");
                    } else {
                        url.append("tin=");
                    }
                    url.append(tin).append("&facturaDate=").append(date.format(DateUtils.DAY_MONTH_YEAR_DOT_FORMATTER));
                    try {
                        ResponseEntity<String> response = restTemplate.exchange(url.toString(), HttpMethod.GET, new HttpEntity<>(getHeaders()), String.class);
                        log.info("Get company Vat Registration response: {}", response);
                        if (HttpStatus.OK == response.getStatusCode()) {
                            return objectMapper.readValue(response.getBody(), VATCompanyDateDTO.class);
                        } else {
                            log.error("Get Company Vat By Date response: {}", response);
                            if (throwException) {
                                throw new IntegrationException(localizationService.localize(MessageConstants.GET_COMPANY_VAT_ERROR), response.getBody(), response.getStatusCode());
                            } else {
                                return null;
                            }
                        }
                    } catch (Exception e) {
                        log.error("Json parse error: {}", e.getMessage());
                        if (throwException) {
                            throw new IntegrationException(localizationService.localize(MessageConstants.GET_COMPANY_VAT_ERROR), e.getMessage());
                        } else {
                            return null;
                        }
                    }
                },
                throwable -> {
                    log.error(throwable.getMessage(), throwable);
                    if (throwException) {
                        throw new IntegrationException(throwable.getMessage());
                    }
                    return null;
                }
        );
    }

    public TaxCompanyBranchDTO[] getBranchesByCompany(String tin, boolean throwException) {
        CircuitBreaker cb = circuitBreakerFactory.create(getIntegrationType().name());
        return cb.run(
                () -> {
                    String companyTin = Optional.ofNullable(tin).orElse(SecurityUtils.getCurrentCompanyTin());
                    StringBuilder url = new StringBuilder();
                    url.append(this.getFacturaHost());
                    url.append("/mysoliq/services");
                    url.append("/yur-branchs/getdatabytin?tin=").append(companyTin);
                    try {
                        ResponseEntity<String> response = restTemplate.exchange(url.toString(), HttpMethod.GET, new HttpEntity<>(getHeaders()), String.class);
                        if (HttpStatus.OK == response.getStatusCode()) {
                            log.info("Get Branches By Company:{} ", response.getBody());
                            return objectMapper.readValue(response.getBody(), TaxCompanyBranchDTO[].class);
                        } else {
                            log.error("Get Company Vat By Date response: {}", response);
                            if (throwException) {
                                throw new IntegrationException("Error Get company branches by TIN :" + companyTin, response.getBody(), response.getStatusCode());
                            } else {
                                return new TaxCompanyBranchDTO[0];
                            }
                        }
                    } catch (Exception e) {
                        log.error("Json parse error: {}", e.getMessage());
                        if (throwException) {
                            throw new IntegrationException("Error Get company branches by TIN :" + companyTin, e.getMessage());
                        } else {
                            return new TaxCompanyBranchDTO[0];
                        }
                    }
                },
                throwable -> {
                    log.error(throwable.getMessage(), throwable);
                    if (throwException) {
                        throw new IntegrationException(throwable.getMessage());
                    }
                    return new TaxCompanyBranchDTO[0];
                }
        );
    }
    private HttpHeaders getHeaders() {
        HttpHeaders headers = SecurityUtils.getHeader();
        headers.setBasicAuth(applicationProperties.getSoliqConfig().getUsername(), applicationProperties.getSoliqConfig().getPassword());
        return headers;
    }

    private String buildCurl(String url) {
        StringBuilder curl = new StringBuilder("curl -i -X GET '").append(url).append("'");
        getHeaders().forEach((name, values) ->
                values.forEach(value -> curl.append(" -H '").append(name).append(": ").append(value).append("'"))
        );
        return curl.toString();
    }
}
