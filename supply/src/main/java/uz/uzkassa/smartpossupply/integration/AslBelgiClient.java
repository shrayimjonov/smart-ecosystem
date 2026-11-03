package uz.uzkassa.smartpossupply.integration;

import uz.uzkassa.smartpossupply.constants.MessageConstants;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.hc.core5.http.NameValuePair;
import org.apache.hc.core5.http.message.BasicNameValuePair;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.cloud.client.circuitbreaker.CircuitBreaker;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;
import uz.uzkassa.smartpossupply.config.httpclient.CustomResponse;
import uz.uzkassa.smartpossupply.constants.Constants;
import uz.uzkassa.smartpossupply.dto.aic.AicStockInDetailsDTO;
import uz.uzkassa.smartpossupply.dto.aslbelgi.*;
import uz.uzkassa.smartpossupply.dto.aslbelgi.aic.*;
import uz.uzkassa.smartpossupply.dto.aslbelgi.aic.xtrace.AicDocumentXtraceRequest;
import uz.uzkassa.smartpossupply.dto.aslbelgi.aic.xtrace.GeneratedAicResponse;
import uz.uzkassa.smartpossupply.dto.aslbelgi.xtrace.CheckInfoRequest;
import uz.uzkassa.smartpossupply.dto.aslbelgi.xtrace.privateinfo.ChildPackageData;
import uz.uzkassa.smartpossupply.dto.aslbelgi.xtrace.privateinfo.CisPrivateInfoResponse;
import uz.uzkassa.smartpossupply.dto.aslbelgi.xtrace.privateinfo.CisPrivateInfoResult;
import uz.uzkassa.smartpossupply.dto.aslbelgi.xtrace.privateinfo.MarkingDate;
import uz.uzkassa.smartpossupply.dto.aslbelgi.xtrace.publicinfo.CisPublicInfoResponse;
import uz.uzkassa.smartpossupply.dto.integration.turon.aggregation.TuronAggregationOrderApiErrorResponse;
import uz.uzkassa.smartpossupply.dto.stockadjustment.WithdrawRequestDTO;
import uz.uzkassa.smartpossupply.dto.stockadjustment.WithdrawResponseDTO;
import uz.uzkassa.smartpossupply.enums.CodeType;
import uz.uzkassa.smartpossupply.enums.ProductGroup;
import uz.uzkassa.smartpossupply.enums.ProductType;
import uz.uzkassa.smartpossupply.enums.TuronMarkStatus;
import uz.uzkassa.smartpossupply.utils.MarkUtils;
import uz.uzkassa.smartpossupply.utils.SecurityUtils;
import uz.uzkassa.smartpossupply.web.rest.errors.BadRequestException;
import uz.uzkassa.smartpossupply.web.rest.errors.IntegrationException;
import uz.uzkassa.smartpossupply.web.rest.errors.XtraceException;

import java.net.URI;
import java.util.*;

@Component
@Slf4j
public class AslBelgiClient extends BaseClient {

    @Qualifier("aslBelgiRestTemplate")
    private final RestTemplate aslBelgiRestTemplate;

    public AslBelgiClient(RestTemplate aslBelgiRestTemplate) {
        this.aslBelgiRestTemplate = aslBelgiRestTemplate;
    }

    public IntegrationType getIntegrationType() {
        return IntegrationType.ASL_BELGI;
    }

    public AuthKey getAuthKey() {
        CircuitBreaker cb = circuitBreakerFactory.create(getIntegrationType().name());
        return cb.run(() -> {
            String url = applicationProperties.getAslBelgiConfig().getUrl().concat("auth/key");
            ResponseEntity<String> response;
            try {
                response = aslBelgiRestTemplate.exchange(
                        url,
                        HttpMethod.GET,
                        new HttpEntity<>(SecurityUtils.getHeader()),
                        String.class
                );
            } catch (Exception e) {
                log.error(e.getMessage(), e);
                throw new IntegrationException(localizationService.localize(MessageConstants.ASL_BELGI_ERROR_AUTH_KEY_ERROR), e.getMessage());
            }
            if (HttpStatus.OK == response.getStatusCode()) {
                try {
                    return objectMapper.readValue(response.getBody(), AuthKey.class);
                } catch (JsonProcessingException e) {
                    log.error("Json parse error: {}", e.getMessage());
                    throw new IntegrationException(localizationService.localize(MessageConstants.ASL_BELGI_ERROR_AUTH_KEY_ERROR), e.getMessage());
                }
            } else {
                log.error("Get Asl Belgi Auth Key error response: {}", response);
                throw new IntegrationException(localizationService.localize(MessageConstants.ASL_BELGI_ERROR_AUTH_KEY_ERROR), response.getBody(), HttpStatus.valueOf(response.getStatusCode().value()));
            }
        }, throwable -> {
            log.error(throwable.getMessage(), throwable);
            throw new IntegrationException(throwable.getMessage());
        });
    }

    public String signIn(AuthKey authKey) {
        CircuitBreaker cb = circuitBreakerFactory.create(getIntegrationType().name());
        return cb.run(() -> {
            String url = applicationProperties.getAslBelgiConfig().getUrl().concat("auth/simpleSignIn");
            try {
                ResponseEntity<String> response = aslBelgiRestTemplate.exchange(
                        url,
                        HttpMethod.POST,
                        new HttpEntity<>(authKey, SecurityUtils.getHeader()), String.class);
                if (HttpStatus.OK == response.getStatusCode()) {
                    AuthToken authToken = objectMapper.readValue(response.getBody(), AuthToken.class);
                    return authToken.getToken();
                } else {
                    log.error("Asl Belgi Sign In response: {}", response);
                    throw new IntegrationException(localizationService.localize(MessageConstants.SIGN_IN_ERROR_ASL_BELGI), response.getBody(), HttpStatus.valueOf(response.getStatusCode().value()));
                }
            } catch (Exception e) {
                log.error("Json parse error: {}", e.getMessage());
                throw new IntegrationException(localizationService.localize(MessageConstants.SIGN_IN_ERROR_ASL_BELGI), e.getMessage());
            }
        }, throwable -> {
            log.error(throwable.getMessage(), throwable);
            throw new IntegrationException(throwable.getMessage());
        });
    }

    public String refresh(String token, String tin) {
        CircuitBreaker cb = circuitBreakerFactory.create(getIntegrationType().name());
        return cb.run(() -> {
            String url = applicationProperties.getAslBelgiConfig().getUrl().concat("auth/refresh");
            HttpHeaders headers = SecurityUtils.getHeader();
            headers.setBearerAuth(token);
            try {
                ResponseEntity<String> response = aslBelgiRestTemplate.exchange(
                        url,
                        HttpMethod.POST,
                        new HttpEntity<>(headers), String.class);
                if (HttpStatus.OK == response.getStatusCode()) {
                    AuthToken authToken = objectMapper.readValue(response.getBody(), AuthToken.class);
                    return authToken.getToken();
                } else {
                    log.error("Asl Belgi Refresh Token response: {}, {}", response, tin);
                    throw new IntegrationException(localizationService.localize(MessageConstants.ASL_BELGI_REFRESH_TOKEN_ERROR), response.getBody(), HttpStatus.valueOf(response.getStatusCode().value()));
                }
            } catch (Exception e) {
                log.error("Json parse error: {}", e.getMessage());
                throw new IntegrationException(localizationService.localize(MessageConstants.ASL_BELGI_REFRESH_TOKEN_ERROR), e.getMessage());
            }
        }, throwable -> {
            log.error(throwable.getMessage(), throwable);
            throw new IntegrationException(throwable.getMessage());
        });
    }

    public CisInfoResponseResult checkCisesInfo(
            String token,
            List<String> aggregationCodes,
            List<String> markCodes,
            List<String> aggregationBlockCodes,
            ProductType productType
    ) {
        return this.checkCisesInfo(token, aggregationCodes, markCodes, aggregationBlockCodes, productType, false);
    }

    public CisInfoResponseResult checkCisesInfo(
            String token,
            List<String> aggregationCodes,
            List<String> markCodes,
            List<String> aggregationBlockCodes,
            ProductType productType,
            boolean fromFactura
    ) {
        if (StringUtils.isEmpty(token)) {
            throw new IntegrationException(localizationService.localize(MessageConstants.ORGANIZATION_TOKEN_EMPTY_ERROR));
        }

        CircuitBreaker cb = circuitBreakerFactory.create(getIntegrationType().name());
        return cb.run(() -> {
            boolean isAggregation = !CollectionUtils.isEmpty(aggregationCodes);
            Map<String, String> codesMap = new HashMap<>();

            if (!CollectionUtils.isEmpty(aggregationCodes)) {
                aggregationCodes.forEach(originalCode -> codesMap.put(fromFactura ? originalCode : MarkUtils.clearAggregationCode(originalCode), originalCode));
            }
            if (!CollectionUtils.isEmpty(markCodes)) {
                markCodes.forEach(originalCode -> codesMap.put(fromFactura ? originalCode : MarkUtils.clearOriginalCode(originalCode, productType), originalCode));
            }
            if (!CollectionUtils.isEmpty(aggregationBlockCodes)) {
                ProductType finalProductType = productType;
                if (ProductType.Alcohol == productType) {
                    finalProductType = ProductType.AlcoholBlock;
                } else if (ProductType.Cigarette == productType) {
                    finalProductType = ProductType.CigaretteBlock;
                } else if (ProductType.Beer == productType) {
                    finalProductType = ProductType.BeerBlock;
                }
                for (String originalCode : aggregationBlockCodes) {
                    codesMap.put(fromFactura ? originalCode : MarkUtils.clearOriginalCode(originalCode, finalProductType), originalCode);
                }
            }

            UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(applicationProperties.getAslBelgiConfig().getUrl() + "cises/info");
            HttpHeaders headers = SecurityUtils.getHeader();
            headers.setBearerAuth(token);
            CisInfoResponseResult cisInfoResponseResult = new CisInfoResponseResult();
            cisInfoResponseResult.setCisInfoResponses(new ArrayList<>());
            for (List<String> codes : Lists.partition(new ArrayList<>(codesMap.keySet()), Constants.COMMON_BATCH_SIZE)) {
                HttpEntity<?> entity = new HttpEntity<>(codes, headers);
                try {
                    ResponseEntity<List<CisInfoResponse>> response = aslBelgiRestTemplate.exchange(
                            builder.toUriString(),
                            HttpMethod.POST,
                            entity,
                            new ParameterizedTypeReference<>() {
                            }
                    );
                    List<CisInfoResponse> result = response.getBody();
                    if (CollectionUtils.isEmpty(result)) {
                        return null;
                    }

                    Map<String, CisInfoResponse> resultMap = new HashMap<>();
                    for (CisInfoResponse cisInfoResponse : result) {
                        CisInfo cisInfo = cisInfoResponse.getCisInfo();
                        String originalCode = Optional.ofNullable(codesMap.get(cisInfo.getRequestedCis())).orElse(codesMap.get(cisInfo.getRequestedCisOriginal(productGroupOf(productType))));
                        cisInfo.setOriginalCode(originalCode);
                        String code = isAggregation ? MarkUtils.clearAggregationCode(cisInfo.getRequestedCis()) : MarkUtils.clearOriginalCode(cisInfo.getRequestedCis(), productType);
                        resultMap.put(code, new CisInfoResponse(cisInfo));
                    }
                    cisInfoResponseResult.getCisInfoResponses().addAll(resultMap.values());
                } catch (Exception e) {
                    log.error(e.getMessage(), e);
                    throw new IntegrationException(localizationService.localize(MessageConstants.ASL_BELGI_ERROR), e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
                }
            }
            return cisInfoResponseResult;
        }, throwable -> {
            log.error(throwable.getMessage(), throwable);
            throw new IntegrationException(throwable.getMessage());
        });
    }

    public Map<String, TuronMarkStatus> checkMarksCisesInfo(
            String token,
            Set<String> snTins
    ) {
        if (StringUtils.isEmpty(token)) {
            throw new IntegrationException(localizationService.localize(MessageConstants.ORGANIZATION_TOKEN_EMPTY_ERROR));
        }

        CircuitBreaker cb = circuitBreakerFactory.create(getIntegrationType().name());
        return cb.run(() -> {
            UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(applicationProperties.getAslBelgiConfig().getUrl() + "cises/info");
            HttpHeaders headers = SecurityUtils.getHeader();
            headers.setBearerAuth(token);
            HttpEntity<?> entity = new HttpEntity<>(snTins, headers);
            try {
                ResponseEntity<List<CisInfoResponse>> response = aslBelgiRestTemplate.exchange(
                        builder.toUriString(),
                        HttpMethod.POST,
                        entity,
                        new ParameterizedTypeReference<>() {
                        }
                );
                List<CisInfoResponse> result = response.getBody();
                if (CollectionUtils.isEmpty(result)) {
                    return null;
                }
                Map<String, TuronMarkStatus> cisInfoResponses = new HashMap<>();
                result.forEach(cisInfoResponse -> {
                    CisInfo cisInfo = cisInfoResponse.getCisInfo();
                    cisInfoResponses.put(cisInfo.getRequestedCis(), cisInfo.getStatus());
                });
                return cisInfoResponses;
            } catch (Exception e) {
                log.error(e.getMessage(), e);
                throw new IntegrationException(localizationService.localize(MessageConstants.ASL_BELGI_ERROR), e.getMessage());
            }
        }, throwable -> {
            log.error(throwable.getMessage(), throwable);
            throw new IntegrationException(throwable.getMessage());
        });
    }

    public CisInfoResponseResult checkAggregatedPublicList(String token, List<String> codes, CodeType codeType, ProductType productType, boolean clearCode) {
        return switch (codeType) {
            case AGGREGATION -> this.checkAggregatedPublicList(token, codes, null, null, productType, clearCode);
            case MARK -> this.checkAggregatedPublicList(token, null, codes, null, productType, clearCode);
            case BLOCK -> this.checkAggregatedPublicList(token, null, null, codes, productType, clearCode);
        };
    }

    public CisInfoResponseResult checkAggregatedPublicList(String token, List<String> codes, CodeType codeType, ProductType productType) {
        return switch (codeType) {
            case AGGREGATION -> this.checkAggregatedPublicList(token, codes, null, null, productType, false);
            case MARK -> this.checkAggregatedPublicList(token, null, codes, null, productType, false);
            case BLOCK -> this.checkAggregatedPublicList(token, null, null, codes, productType, false);
        };
    }

    public CisInfoResponseResult checkAggregatedPublicList(
            String token,
            List<String> aggregationCodes,
            List<String> markCodes,
            List<String> aggregationBlockCodes,
            ProductType productType
    ) {
        return this.checkAggregatedPublicList(token, aggregationCodes, markCodes, aggregationBlockCodes, productType, false);
    }

    public CisInfoResponseResult checkAggregatedPublicList(
            String token,
            List<String> aggregationCodes,
            List<String> markCodes,
            List<String> aggregationBlockCodes,
            ProductType productType,
            boolean clearCode
    ) {
        if (StringUtils.isEmpty(token)) {
            throw new IntegrationException("organization.token.empty.error");
        }

        CircuitBreaker cb = circuitBreakerFactory.create(getIntegrationType().name());
        return cb.run(() -> {
            boolean isAggregation = !CollectionUtils.isEmpty(aggregationCodes);
            Map<String, String> codesMap = new HashMap<>();

            if (!CollectionUtils.isEmpty(aggregationCodes)) {
                aggregationCodes.forEach(originalCode -> codesMap.put(clearCode ? originalCode : MarkUtils.clearAggregationCode(originalCode), originalCode));
            }
            if (!CollectionUtils.isEmpty(markCodes)) {
                markCodes.forEach(originalCode -> codesMap.put(clearCode ? originalCode : MarkUtils.clearOriginalCode(originalCode, productType), originalCode));
            }
            if (!CollectionUtils.isEmpty(aggregationBlockCodes)) {
                ProductType finalProductType = productType;
                if (ProductType.Alcohol == productType) {
                    finalProductType = ProductType.AlcoholBlock;
                } else if (ProductType.Cigarette == productType) {
                    finalProductType = ProductType.CigaretteBlock;
                } else if (ProductType.Beer == productType) {
                    finalProductType = ProductType.BeerBlock;
                }
                for (String originalCode : aggregationBlockCodes) {
                    codesMap.put(clearCode ? originalCode : MarkUtils.clearOriginalCode(originalCode, finalProductType), originalCode);
                }
            }

            // The public-info request requires the product group in the "pg" query parameter, so a
            // null productType makes the request meaningless. Fail fast with a clear message instead of NPE.
            if (productType == null) {
                throw new IntegrationException(localizationService.localize(MessageConstants.ASL_BELGI_ERROR), "productType is required for Asl Belgi public info request", HttpStatus.BAD_REQUEST);
            }
            UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(
                    applicationProperties.getAslBelgiConfig().getUrl() +
                            "cises/publicInfo?pg=" +
                            productType.getProductGroup().getProductGroup()
            );
            HttpHeaders headers = SecurityUtils.getHeader();
            headers.setBearerAuth(token);
            CisInfoResponseResult cisInfoResponseResult = new CisInfoResponseResult();
            cisInfoResponseResult.setCisInfoResponses(new ArrayList<>());
            for (List<String> codes : Lists.partition(new ArrayList<>(codesMap.keySet()), 1000)) {
                HttpEntity<?> entity = new HttpEntity<>(codes, headers);
                try {
                    ResponseEntity<List<CisInfoResponse>> response = aslBelgiRestTemplate.exchange(
                            builder.toUriString(),
                            HttpMethod.POST,
                            entity,
                            new ParameterizedTypeReference<>() {
                            }
                    );
                    List<CisInfoResponse> result = response.getBody();
                    if (CollectionUtils.isEmpty(result)) {
                        return null;
                    }

                    Map<String, CisInfoResponse> resultMap = new HashMap<>();
                    for (CisInfoResponse cisInfoResponse : result) {
                        CisInfo cisInfo = cisInfoResponse.getCisInfo();
                        String originalCode = Optional.ofNullable(codesMap.get(cisInfo.getRequestedCis())).orElse(codesMap.get(cisInfo.getRequestedCisOriginal(productGroupOf(productType))));
                        cisInfo.setOriginalCode(originalCode);
                        String code = isAggregation ? MarkUtils.clearAggregationCode(cisInfo.getRequestedCis()) : MarkUtils.clearOriginalCode(cisInfo.getRequestedCis(), productType);
                        if (resultMap.get(code) == null) {
                            resultMap.put(code, new CisInfoResponse(cisInfo));
                        } else {
                            CisInfoResponse oldCisInfoResponse = resultMap.get(code);
                            if (oldCisInfoResponse.getCisInfo().getStatus() == null && cisInfo.getStatus() != null) {
                                resultMap.remove(code);
                                resultMap.put(code, new CisInfoResponse(cisInfo));
                            }
                        }
                    }
                    cisInfoResponseResult.getCisInfoResponses().addAll(resultMap.values());
                } catch (Exception e) {
                    log.error(e.getMessage(), e);
                    throw new IntegrationException(localizationService.localize(MessageConstants.ASL_BELGI_ERROR), e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
                }
            }
            return cisInfoResponseResult;
        }, throwable -> {
            log.error(throwable.getMessage(), throwable);
            throw new IntegrationException(throwable.getMessage());
        });
    }

    public CisInfoResponseResult checkPrivateInfo(String token, List<String> codes, CodeType codeType, ProductType productType, boolean clearCode) {
        return switch (codeType) {
            case AGGREGATION -> this.checkPrivateInfo(token, codes, null, null, productType, clearCode);
            case MARK -> this.checkPrivateInfo(token, null, codes, null, productType, clearCode);
            case BLOCK -> this.checkPrivateInfo(token, null, null, codes, productType, clearCode);
        };
    }

    public CisInfoResponseResult checkPrivateInfo(String token, List<String> codes, CodeType codeType, ProductType productType) {
        return switch (codeType) {
            case AGGREGATION -> this.checkPrivateInfo(token, codes, null, null, productType, false);
            case MARK -> this.checkPrivateInfo(token, null, codes, null, productType, false);
            case BLOCK -> this.checkPrivateInfo(token, null, null, codes, productType, false);
        };
    }

    public CisInfoResponseResult checkPrivateInfoForStockInFactura(String token, List<String> aggregationCodes, List<String> markCodes, List<String> aggregationBlockCodes, ProductType productType) {
        return this.checkPrivateInfo(token, aggregationCodes, markCodes, aggregationBlockCodes, productType, true);
    }

    public CisInfoResponseResult checkPrivateInfo(
            String token,
            List<String> aggregationCodes,
            List<String> markCodes,
            List<String> aggregationBlockCodes,
            ProductType productType
    ) {
        return this.checkPrivateInfo(token, aggregationCodes, markCodes, aggregationBlockCodes, productType, false);
    }

    /**
     * Xtrace check CIS private info
     */
    public CisInfoResponseResult checkPrivateInfo(
            String token,
            List<String> aggregationCodes,
            List<String> markCodes,
            List<String> aggregationBlockCodes,
            ProductType productType,
            boolean fromFactura
    ) {
        if (StringUtils.isEmpty(token)) {
            throw new IntegrationException(localizationService.localize(MessageConstants.ORGANIZATION_TOKEN_EMPTY_ERROR));
        }

        CircuitBreaker cb = circuitBreakerFactory.create(getIntegrationType().name());
        return cb.run(() -> {
            boolean isAggregation = !CollectionUtils.isEmpty(aggregationCodes);
            Map<String, String> codesMap = new HashMap<>();

            if (!CollectionUtils.isEmpty(aggregationCodes)) {
                aggregationCodes.forEach(originalCode -> {
                    if (fromFactura) {
                        codesMap.put(originalCode, MarkUtils.addZerosToAggregationCode(originalCode));
                    } else {
                        codesMap.put(originalCode, originalCode);
                    }
                });
            }
            if (!CollectionUtils.isEmpty(markCodes)) {
                markCodes.forEach(originalCode -> codesMap.put(fromFactura ? originalCode : MarkUtils.clearOriginalCode(originalCode, productType), originalCode));
            }
            if (!CollectionUtils.isEmpty(aggregationBlockCodes)) {
                ProductType finalProductType = productType;
                if (ProductType.Alcohol == productType) {
                    finalProductType = ProductType.AlcoholBlock;
                } else if (ProductType.Cigarette == productType) {
                    finalProductType = ProductType.CigaretteBlock;
                } else if (ProductType.Beer == productType) {
                    finalProductType = ProductType.BeerBlock;
                }
                for (String originalCode : aggregationBlockCodes) {
                    codesMap.put(fromFactura ? originalCode : MarkUtils.clearOriginalCode(originalCode, finalProductType), originalCode);
                }
            }

            UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(applicationProperties.getAslBelgiConfig().getPrivateCheckInfoUrl());
            HttpHeaders headers = SecurityUtils.getHeader();
            headers.setBearerAuth(token);
            CisInfoResponseResult cisInfoResponseResult = new CisInfoResponseResult();
            cisInfoResponseResult.setCisInfoResponses(new ArrayList<>());
            for (List<String> codes : Lists.partition(new ArrayList<>(codesMap.keySet()), Constants.COMMON_BATCH_SIZE)) {
                HttpEntity<?> entity = new HttpEntity<>(new CheckInfoRequest(codes), headers);
                try {
                    ResponseEntity<String> response = aslBelgiRestTemplate.exchange(
                            builder.toUriString(),
                            HttpMethod.POST,
                            entity,
                            new ParameterizedTypeReference<>() {
                            }
                    );
                    if (response.getStatusCode() == HttpStatus.TEMPORARY_REDIRECT && response.getHeaders().getLocation() != null) {
                        URI redirectUri = response.getHeaders().getLocation();
                        response = restTemplate.exchange(redirectUri.isAbsolute() ? redirectUri : new URI(applicationProperties.getAslBelgiConfig().getBaseXtraceUrl()).resolve(redirectUri), HttpMethod.POST, entity, new ParameterizedTypeReference<>() {
                        });
                    }
                    JsonNode jsonNode = objectMapper.readTree(response.getBody());
                    if (jsonNode.isArray() && jsonNode.size() > 0 && jsonNode.get(0).has("cisInfo")) {
                        List<CisInfoResponse> result = objectMapper.readValue(response.getBody(), new TypeReference<>() {
                        });
                        if (CollectionUtils.isEmpty(result)) {
                            return null;
                        }

                        Map<String, CisInfoResponse> resultMap = new HashMap<>();
                        for (CisInfoResponse cisInfoResponse : result) {
                            CisInfo cisInfo = cisInfoResponse.getCisInfo();
                            String originalCode = Optional.ofNullable(codesMap.get(cisInfo.getRequestedCis())).orElse(codesMap.get(cisInfo.getRequestedCisOriginal(productGroupOf(productType))));
                            cisInfo.setOriginalCode(originalCode);
                            String code = isAggregation ? MarkUtils.clearAggregationCode(cisInfo.getRequestedCis()) : MarkUtils.clearOriginalCode(cisInfo.getRequestedCis(), productType);
                            if (resultMap.get(code) == null) {
                                resultMap.put(code, new CisInfoResponse(cisInfo));
                            } else {
                                CisInfoResponse oldCisInfoResponse = resultMap.get(code);
                                if (oldCisInfoResponse.getCisInfo().getStatus() == null && cisInfo.getStatus() != null) {
                                    resultMap.remove(code);
                                    resultMap.put(code, new CisInfoResponse(cisInfo));
                                }
                            }
                        }
                        cisInfoResponseResult.getCisInfoResponses().addAll(resultMap.values());
                    } else if (jsonNode.has("results")) {
                        CisPrivateInfoResponse result = objectMapper.readValue(response.getBody(), CisPrivateInfoResponse.class);
                        if (result == null || CollectionUtils.isEmpty(result.getResults())) {
                            return null;
                        }

                        Map<String, CisInfoResponse> resultMap = new HashMap<>();
                        for (CisPrivateInfoResult cisPrivateInfoResult : result.getResults()) {
                            CisInfo cisInfo = this.mapPrivateInfo(cisPrivateInfoResult);
                            cisInfo.setOriginalCode(Optional.ofNullable(codesMap.get(cisInfo.getRequestedCis())).orElse(codesMap.get(cisInfo.getRequestedCisOriginal(productGroupOf(productType)))));
                            String code = isAggregation ? MarkUtils.clearAggregationCode(cisInfo.getRequestedCis()) : MarkUtils.clearOriginalCode(cisInfo.getRequestedCis(), productType);
                            resultMap.put(code, new CisInfoResponse(cisInfo));
                        }
                        cisInfoResponseResult.getCisInfoResponses().addAll(resultMap.values());
                    } else if (jsonNode.isArray() && !jsonNode.isEmpty() && jsonNode.get(0).has("isHadExtendedCode")) {
                        List<CisPublicInfoResponse> result = objectMapper.readValue(response.getBody(), new TypeReference<>() {
                        });
                        if (CollectionUtils.isEmpty(result)) {
                            return null;
                        }

                        Map<String, CisInfoResponse> resultMap = new HashMap<>();
                        for (CisPublicInfoResponse cisPublicInfoResponse : result) {
                            CisInfo cisInfo = this.mapPublicInfo(cisPublicInfoResponse);
                            cisInfo.setOriginalCode(Optional.ofNullable(codesMap.get(cisInfo.getRequestedCis())).orElse(codesMap.get(cisInfo.getRequestedCisOriginal(productGroupOf(productType)))));
                            String code = isAggregation ? MarkUtils.clearAggregationCode(cisInfo.getRequestedCis()) : MarkUtils.clearOriginalCode(cisInfo.getRequestedCis(), productType);
                            if (resultMap.get(code) == null) {
                                resultMap.put(code, new CisInfoResponse(cisInfo));
                            } else {
                                CisInfoResponse oldCisInfoResponse = resultMap.get(code);
                                if (oldCisInfoResponse.getCisInfo().getStatus() == null && cisInfo.getStatus() != null) {
                                    resultMap.remove(code);
                                    resultMap.put(code, new CisInfoResponse(cisInfo));
                                }
                            }
                        }
                        cisInfoResponseResult.getCisInfoResponses().addAll(resultMap.values());
                    } else if (jsonNode.isArray() && jsonNode.isEmpty()) {
                        return null;
                    } else {
                        throw new XtraceException(localizationService.localize(MessageConstants.ASL_BELGI_XTRACE_ERROR), response.getBody());
                    }
                } catch (Exception e) {
                    log.error(e.getMessage(), e);
                    throw new XtraceException(localizationService.localize(MessageConstants.ASL_BELGI_XTRACE_ERROR), e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
                }
            }
            return cisInfoResponseResult;
        }, throwable -> {
            log.error(throwable.getMessage(), throwable);
            throw new IntegrationException(throwable.getMessage());
        });
    }

    private static ProductGroup productGroupOf(ProductType productType) {
        return productType != null ? productType.getProductGroup() : null;
    }

    private CisInfo mapPrivateInfo(CisPrivateInfoResult cisPrivateInfoResult) {
        CisInfo cisInfo = new CisInfo();
        cisInfo.setCis(cisPrivateInfoResult.getCodeData().getCode());
        cisInfo.setRequestedCis(cisPrivateInfoResult.getCodeData().getCode());
        cisInfo.setStatus(TuronMarkStatus.valueOf(cisPrivateInfoResult.getCodeData().getStatus()));
        cisInfo.setPackageType(cisPrivateInfoResult.getPackageData().getPackageType());
        cisInfo.setProductGroupId(cisPrivateInfoResult.getProductData().getProductGroupId());
        cisInfo.setProducedDate(cisPrivateInfoResult.getProductData().getProductionDate());
        cisInfo.setEmissionDate(Optional.ofNullable(cisPrivateInfoResult.getMarkingData())
                .map(MarkingDate::getEmissionDate)
                .orElse(null));

        cisInfo.setIssueDate(Optional.ofNullable(cisPrivateInfoResult.getMarkingData())
                .map(MarkingDate::getIssueDate)
                .orElse(null));

        cisInfo.setExpirationDate(cisPrivateInfoResult.getProductData().getExpirationDate());
        if (cisPrivateInfoResult.getTurnoverData().getOwnerInfo() != null) {
            cisInfo.setOwnerInn(cisPrivateInfoResult.getTurnoverData().getOwnerInfo().getOwnerTin());
            if (!CollectionUtils.isEmpty(cisPrivateInfoResult.getTurnoverData().getOwnerInfo().getOwnerName())) {
                cisInfo.setOwnerName(cisPrivateInfoResult.getTurnoverData().getOwnerInfo().getOwnerName().get("ru"));
            }
        }
        cisInfo.setChild(cisPrivateInfoResult.getPackageData().getChildren().stream().map(ChildPackageData::getCode).toList());
        return cisInfo;
    }

    public CisInfoResponseResult checkPublicInfo(
            String token,
            List<String> aggregationCodes,
            List<String> markCodes,
            List<String> aggregationBlockCodes,
            ProductType productType
    ) {
        return this.checkPublicInfo(token, aggregationCodes, markCodes, aggregationBlockCodes, productType, false);
    }

    /**
     * Xtrace check CIS public info
     */
    public CisInfoResponseResult checkPublicInfo(
            String token,
            List<String> aggregationCodes,
            List<String> markCodes,
            List<String> aggregationBlockCodes,
            ProductType productType,
            boolean clearCode
    ) {
        if (StringUtils.isEmpty(token)) {
            throw new IntegrationException("organization.token.empty.error");
        }

        CircuitBreaker cb = circuitBreakerFactory.create(getIntegrationType().name());
        return cb.run(() -> {
            boolean isAggregation = !CollectionUtils.isEmpty(aggregationCodes);
            Map<String, String> codesMap = new HashMap<>();

            if (!CollectionUtils.isEmpty(aggregationCodes)) {
                aggregationCodes.forEach(originalCode -> codesMap.put(clearCode ? originalCode : MarkUtils.clearAggregationCode(originalCode), originalCode));
            }
            if (!CollectionUtils.isEmpty(markCodes)) {
                markCodes.forEach(originalCode -> codesMap.put(clearCode ? originalCode : MarkUtils.clearOriginalCode(originalCode, productType), originalCode));
            }
            if (!CollectionUtils.isEmpty(aggregationBlockCodes)) {
                ProductType finalProductType = productType;
                if (ProductType.Alcohol == productType) {
                    finalProductType = ProductType.AlcoholBlock;
                } else if (ProductType.Cigarette == productType) {
                    finalProductType = ProductType.CigaretteBlock;
                } else if (ProductType.Beer == productType) {
                    finalProductType = ProductType.BeerBlock;
                }
                for (String originalCode : aggregationBlockCodes) {
                    codesMap.put(clearCode ? originalCode : MarkUtils.clearOriginalCode(originalCode, finalProductType), originalCode);
                }
            }

            UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(applicationProperties.getAslBelgiConfig().getPublicCheckInfoUrl());
            HttpHeaders headers = SecurityUtils.getHeader();
            headers.setBearerAuth(token);
            CisInfoResponseResult cisInfoResponseResult = new CisInfoResponseResult();
            cisInfoResponseResult.setCisInfoResponses(new ArrayList<>());
            for (List<String> codes : Lists.partition(new ArrayList<>(codesMap.keySet()), 1000)) {
                HttpEntity<?> entity = new HttpEntity<>(new CheckInfoRequest(codes), headers);
                try {
                    ResponseEntity<List<CisPublicInfoResponse>> response = aslBelgiRestTemplate.exchange(
                            builder.toUriString(),
                            HttpMethod.POST,
                            entity,
                            new ParameterizedTypeReference<>() {
                            }
                    );
                    List<CisPublicInfoResponse> result = response.getBody();
                    if (CollectionUtils.isEmpty(result)) {
                        return null;
                    }

                    Map<String, CisInfoResponse> resultMap = new HashMap<>();
                    for (CisPublicInfoResponse cisPublicInfoResponse : result) {
                        CisInfo cisInfo = this.mapPublicInfo(cisPublicInfoResponse);
                        cisInfo.setOriginalCode(Optional.ofNullable(codesMap.get(cisInfo.getRequestedCis())).orElse(codesMap.get(cisInfo.getRequestedCisOriginal(productGroupOf(productType)))));
                        String code = isAggregation ? MarkUtils.clearAggregationCode(cisInfo.getRequestedCis()) : MarkUtils.clearOriginalCode(cisInfo.getRequestedCis(), productType);
                        if (resultMap.get(code) == null) {
                            resultMap.put(code, new CisInfoResponse(cisInfo));
                        } else {
                            CisInfoResponse oldCisInfoResponse = resultMap.get(code);
                            if (oldCisInfoResponse.getCisInfo().getStatus() == null && cisInfo.getStatus() != null) {
                                resultMap.remove(code);
                                resultMap.put(code, new CisInfoResponse(cisInfo));
                            }
                        }
                    }
                    cisInfoResponseResult.getCisInfoResponses().addAll(resultMap.values());
                } catch (Exception e) {
                    log.error(e.getMessage(), e);
                    throw new XtraceException(localizationService.localize(MessageConstants.ASL_BELGI_XTRACE_ERROR), e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
                }
            }
            return cisInfoResponseResult;
        }, throwable -> {
            log.error(throwable.getMessage(), throwable);
            throw new IntegrationException(throwable.getMessage());
        });
    }

    private CisInfo mapPublicInfo(CisPublicInfoResponse cisPublicInfoResponse) {
        CisInfo cisInfo = new CisInfo();
        cisInfo.setRequestedCis(cisPublicInfoResponse.getCode());
        cisInfo.setRequestedCis(cisPublicInfoResponse.getCode());
        cisInfo.setStatus(TuronMarkStatus.valueOf(cisPublicInfoResponse.getStatus()));
        cisInfo.setPackageType(cisPublicInfoResponse.getPackageType());
        cisInfo.setProductGroupId(cisPublicInfoResponse.getProductGroupId());
        cisInfo.setProducedDate(cisPublicInfoResponse.getProductionDate());
        cisInfo.setEmissionDate(cisPublicInfoResponse.getEmissionDate());
        cisInfo.setIssueDate(cisPublicInfoResponse.getIssueDate());
        cisInfo.setExpirationDate(cisPublicInfoResponse.getExpirationDate());
        return cisInfo;
    }

    public CisInfoResponse[] cisesInfo(String tin, String token, Set<String> codes) {
        CircuitBreaker cb = circuitBreakerFactory.create(getIntegrationType().name());
        return cb.run(() -> {
            long start = System.currentTimeMillis();
            try {
                String url = applicationProperties.getAslBelgiConfig().getUrl() + "cises/info";
                String json = objectMapper.writeValueAsString(codes);
                CustomResponse response = customRequest.sendPostRequest(url, getHeaders(token), json);
                writeLog("Cises Info", url, response);
                if (response.getCode() == 200) {
                    return objectMapper.readValue(response.getContent(), CisInfoResponse[].class);
                } else {
                    TuronAggregationOrderApiErrorResponse turonAggregationOrderApiErrorResponse = response.getContent() != null
                            ? objectMapper.readValue(response.getContent(), TuronAggregationOrderApiErrorResponse.class)
                            : null;
                    String errorMessage = turonAggregationOrderApiErrorResponse != null
                            ? turonAggregationOrderApiErrorResponse.toString()
                            : response.getErrorMessage();
                    StringBuilder message = new StringBuilder();
                    message.append("TIME: ").append((System.currentTimeMillis() - start)).append("\n");
                    message.append("TIN: ").append(tin).append(" \n");
                    message.append("CODE: ").append(codes.iterator().next()).append(" \n");
                    message.append(errorMessage);
                    throw new IntegrationException(message.toString(), response.getErrorMessage(), HttpStatus.valueOf(response.getCode()));
                }
            } catch (Exception e) {
                log.error("Turon order convert to JSON error: {}", e.getMessage());
                throw new IntegrationException(localizationService.localize(MessageConstants.JSON_CONVERT_ERROR_FROM_TURON), e.getMessage());
            }
        }, throwable -> {
            log.error(throwable.getMessage(), throwable);
            throw new IntegrationException(throwable.getMessage());
        });
    }

    private void writeLog(String method, String api, CustomResponse response) {
        log.info("===================== ".concat(method.toUpperCase(Locale.ROOT)).concat(" ========================="));
        log.info("API:".concat(api));
    }

    private Map<String, String> getHeaders(String token) {
        Map<String, String> headers = new HashMap<>();
        headers.put("Authorization", "Bearer " + token);
        return headers;
    }

    public ProductTnVedResponseList getTnVedCodeFromTrueApi(String token, String productBarcode) {
        CircuitBreaker cb = circuitBreakerFactory.create(getIntegrationType().name());
        return cb.run(() -> {
            try {
                String url = applicationProperties.getAslBelgiConfig().getFacade() + "product/search";
                NameValuePair[] params = new NameValuePair[1];
                params[0] = new BasicNameValuePair("gtin", productBarcode);
                CustomResponse response = customRequest.sendGetRequest(url, getHeaders(token), Arrays.stream(params).toList());
                writeLog("Get Auth Key", url, response);
                if (response.getCode() == 200) {
                    return objectMapper.readValue(response.getContent(), ProductTnVedResponseList.class);
                } else {
                    throw new IntegrationException(response.getErrorMessage(), response.getErrorMessage(), HttpStatus.valueOf(response.getCode()));
                }
            } catch (Exception e) {
                log.error("Turon tnVed response convert to JSON error: {}", e.getMessage());
                throw new IntegrationException(localizationService.localize(MessageConstants.JSON_CONVERT_ERROR_FROM_TURON), e.getMessage());
            }
        }, throwable -> {
            log.error(throwable.getMessage(), throwable);
            throw new IntegrationException(throwable.getMessage());
        });
    }

    /**
     * aic enrichment
     */
    public AicResponse aicEnrichment(String token, AicRequest enrichmentRequest) {
        CircuitBreaker cb = circuitBreakerFactory.create(getIntegrationType().name());
        return cb.run(() -> {
            long start = System.currentTimeMillis();
            try {
                String url = applicationProperties.getAslBelgiConfig().getUrl() + "documents/aic/enrichment";
                String json = objectMapper.writeValueAsString(enrichmentRequest);
                writeLog("AIC ENRICHMENT REQUEST", json, null);
                CustomResponse response = customRequest.sendPostRequest(url, getHeaders(token), json);
                writeLog("AIC ENRICHMENT RESPONSE", url, response);
                if (response.getCode() == 200) {
                    return objectMapper.readValue(response.getContent(), AicResponse.class);
                } else {
                    AicEnrichmentErrorResponse aicEnrichmentErrorResponse = response.getContent() != null
                            ? objectMapper.readValue(response.getContent(), AicEnrichmentErrorResponse.class)
                            : null;
                    String errorMessage = aicEnrichmentErrorResponse != null
                            ? aicEnrichmentErrorResponse.toString()
                            : response.getErrorMessage();
                    StringBuilder message = new StringBuilder();
                    message.append("TIME: ").append((System.currentTimeMillis() - start)).append("\n");
                    message.append("TIN: ").append(enrichmentRequest.getInn()).append(" \n");
                    message.append("CODE: ").append(Arrays.toString(enrichmentRequest.getCises().toArray())).append(" \n");
                    message.append(errorMessage);
                    throw new IntegrationException(message.toString(), response.getErrorMessage(), HttpStatus.valueOf(response.getCode()));
                }
            } catch (Exception e) {
                log.error("AslBelgi aic enrichment convert to JSON error: {}", e.getMessage());
                throw new IntegrationException(localizationService.localize(MessageConstants.JSON_CONVERT_ERROR_FROM_ASL_BELGI), e.getMessage());
            }
        }, throwable -> {
            log.error(throwable.getMessage(), throwable);
            throw new IntegrationException(throwable.getMessage());
        });
    }

    public AicDocumentResponse aicConfirmation(String token, String tin, AicDocumentRequest aicDocumentRequest) {
        CircuitBreaker cb = circuitBreakerFactory.create(getIntegrationType().name());
        return cb.run(() -> {
            long start = System.currentTimeMillis();
            try {
                String url = applicationProperties.getAslBelgiConfig().getUrl() + "documents/aic/confirmation";
                String json = objectMapper.writeValueAsString(aicDocumentRequest);
                writeLog("Confirmation aic document request", json, null);
                CustomResponse response = customRequest.sendPostRequest(url, getHeaders(token), json);
                writeLog("Confirmation aic document response", url, response);
                if (response.getCode() == 200) {
                    return objectMapper.readValue(response.getContent(), AicDocumentResponse.class);
                } else {
                    TuronAggregationOrderApiErrorResponse turonAggregationOrderApiErrorResponse = response.getContent() != null
                            ? objectMapper.readValue(response.getContent(), TuronAggregationOrderApiErrorResponse.class)
                            : null;
                    String errorMessage = turonAggregationOrderApiErrorResponse != null
                            ? turonAggregationOrderApiErrorResponse.toString()
                            : response.getErrorMessage();
                    StringBuilder message = new StringBuilder();
                    message.append("TIME: ").append((System.currentTimeMillis() - start)).append("\n");
                    message.append("INN: ").append(tin).append(" \n");
                    message.append("DOCUMENT: ").append(aicDocumentRequest.getDocument()).append(" \n");
                    message.append(errorMessage);
                    throw new IntegrationException(message.toString(), response.getErrorMessage(), HttpStatus.valueOf(response.getCode()));
                }
            } catch (Exception e) {
                log.error("AslBelgi aic confirmation convert to JSON error: {}", e.getMessage());
                throw new IntegrationException(localizationService.localize(MessageConstants.JSON_CONVERT_ERROR_FROM_ASL_BELGI), e.getMessage());
            }
        }, throwable -> {
            log.error(throwable.getMessage(), throwable);
            throw new IntegrationException(throwable.getMessage());
        });
    }

    public AicDocumentResponse aicDisaggregate(String token, String tin, AicDocumentRequest disaggregateRequest) {
        CircuitBreaker cb = circuitBreakerFactory.create(getIntegrationType().name());
        return cb.run(() -> {
            long start = System.currentTimeMillis();
            try {
                String url = applicationProperties.getAslBelgiConfig().getUrl() + "documents/aic/disaggregation";
                String json = objectMapper.writeValueAsString(disaggregateRequest);
                writeLog("Disaggregation aic document request", json, null);
                CustomResponse response = customRequest.sendPostRequest(url, getHeaders(token), json);
                writeLog("Disaggregation aic document response", url, response);
                if (response.getCode() == 200) {
                    return objectMapper.readValue(response.getContent(), AicDocumentResponse.class);
                } else {
                    TuronAggregationOrderApiErrorResponse turonAggregationOrderApiErrorResponse = response.getContent() != null
                            ? objectMapper.readValue(response.getContent(), TuronAggregationOrderApiErrorResponse.class)
                            : null;
                    String errorMessage = turonAggregationOrderApiErrorResponse != null
                            ? turonAggregationOrderApiErrorResponse.toString()
                            : response.getErrorMessage();
                    StringBuilder message = new StringBuilder();
                    message.append("TIME: ").append((System.currentTimeMillis() - start)).append("\n");
                    message.append("INN: ").append(tin).append(" \n");
                    message.append("DOCUMENT: ").append(disaggregateRequest.getDocument()).append(" \n");
                    message.append(errorMessage);
                    throw new IntegrationException(message.toString(), response.getErrorMessage(), HttpStatus.valueOf(response.getCode()));
                }
            } catch (Exception e) {
                log.error("AslBelgi aic disaggregation convert to JSON error: {}", e.getMessage());
                throw new IntegrationException(localizationService.localize(MessageConstants.JSON_CONVERT_ERROR_FROM_ASL_BELGI), e.getMessage());
            }
        }, throwable -> {
            log.error(throwable.getMessage(), throwable);
            throw new IntegrationException(throwable.getMessage());
        });
    }

    public WithdrawResponseDTO withdraw(String token, String tin, WithdrawRequestDTO withdrawRequestDTO) {
        CircuitBreaker cb = circuitBreakerFactory.create(getIntegrationType().name());
        return cb.run(() -> {
            long start = System.currentTimeMillis();
            try {
                String url = applicationProperties.getAslBelgiConfig().getUrl() + "documents/withdraw";
                HttpHeaders headers = SecurityUtils.getHeader();
                headers.setBearerAuth(token);
                String json = objectMapper.writeValueAsString(withdrawRequestDTO);
                ResponseEntity<String> response = aslBelgiRestTemplate.exchange(
                        url,
                        HttpMethod.POST,
                        new HttpEntity<>(json, headers),
                        String.class
                );
                if (response.getStatusCode().is2xxSuccessful()) {
                    return objectMapper.readValue(response.getBody(), WithdrawResponseDTO.class);
                } else {
                    ErrorResponse turonAggregationOrderApiErrorResponse = response.getBody() != null
                            ? objectMapper.readValue(response.getBody(), ErrorResponse.class)
                            : null;
                    String errorMessage = turonAggregationOrderApiErrorResponse != null
                            ? turonAggregationOrderApiErrorResponse.toString()
                            : null;
                    StringBuilder message = new StringBuilder();
                    message.append("TIME: ").append((System.currentTimeMillis() - start)).append("\n");
                    message.append("INN: ").append(tin).append(" \n");
                    message.append(Optional.ofNullable(errorMessage).orElse(""));
                    throw new IntegrationException(localizationService.localize(MessageConstants.ASL_BELGI_ERROR), message.toString(), response.getStatusCode());
                }
            } catch (Exception e) {
                log.error("Document withdraw convert to JSON error: {}", e.getMessage());
                throw new IntegrationException(localizationService.localize(MessageConstants.JSON_CONVERT_ERROR_FROM_ASL_BELGI), e.getMessage());
            }
        }, throwable -> {
            log.error(throwable.getMessage(), throwable);
            throw new IntegrationException(throwable.getMessage());
        });
    }

    /**
     * Сгенерировать агрегированного импортного кода (АИК) - xtrace
     */
    public String generateAic(String token) {
        CircuitBreaker cb = circuitBreakerFactory.create(getIntegrationType().name());
        return cb.run(() -> {
            UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(applicationProperties.getAslBelgiConfig().getBaseXtraceUrl().concat("/public/api/v1/customs-registry/code"));
            HttpHeaders headers = SecurityUtils.getHeader();
            headers.setBearerAuth(token);
            HttpEntity<?> entity = new HttpEntity<>(headers);
            try {
                ResponseEntity<GeneratedAicResponse> response = aslBelgiRestTemplate.exchange(
                        builder.toUriString(),
                        HttpMethod.POST,
                        entity,
                        new ParameterizedTypeReference<>() {
                        }
                );
                GeneratedAicResponse result = response.getBody();
                return Optional.ofNullable(result).map(GeneratedAicResponse::getAggregatedCustomsCode).orElse(null);
            } catch (Exception e) {
                log.error(e.getMessage(), e);
                throw new XtraceException(localizationService.localize(MessageConstants.ASL_BELGI_XTRACE_ERROR), e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
            }
        }, throwable -> {
            log.error(throwable.getMessage(), throwable);
            throw new IntegrationException(throwable.getMessage());
        });
    }

    /**
     * Зарегистрировать агрегированный импортный код (АИК) - xtrace
     */
    public AicDocumentResponse registerAic(String token, AicDocumentXtraceRequest aicDocumentXtraceRequest) {
        CircuitBreaker cb = circuitBreakerFactory.create(getIntegrationType().name());
        return cb.run(() -> {
            UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(applicationProperties.getAslBelgiConfig().getBaseXtraceUrl().concat("/public/api/v1/doc/customs-code"));
            HttpHeaders headers = SecurityUtils.getHeader();
            headers.setBearerAuth(token);
            HttpEntity<?> entity = new HttpEntity<>(aicDocumentXtraceRequest, headers);
            try {
                ResponseEntity<AicDocumentResponse> response = aslBelgiRestTemplate.exchange(
                        builder.toUriString(),
                        HttpMethod.POST,
                        entity,
                        new ParameterizedTypeReference<>() {
                        }
                );
                return response.getBody();
            } catch (Exception e) {
                log.error(e.getMessage(), e);
                throw new XtraceException(localizationService.localize(MessageConstants.ASL_BELGI_XTRACE_ERROR), e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
            }
        }, throwable -> {
            log.error(throwable.getMessage(), throwable);
            throw new IntegrationException(throwable.getMessage());
        });
    }

    /**
     * Расформировать агрегированный импортный код (АИК) - xtrace
     */
    public AicDocumentResponse disbandAic(String token, AicDocumentXtraceRequest aicDocumentXtraceRequest) {
        CircuitBreaker cb = circuitBreakerFactory.create(getIntegrationType().name());
        return cb.run(() -> {
            UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(applicationProperties.getAslBelgiConfig().getBaseXtraceUrl().concat("/public/api/doc/customs-code-withdrawal"));
            HttpHeaders headers = SecurityUtils.getHeader();
            headers.setBearerAuth(token);
            HttpEntity<?> entity = new HttpEntity<>(aicDocumentXtraceRequest, headers);
            try {
                ResponseEntity<AicDocumentResponse> response = aslBelgiRestTemplate.exchange(
                        builder.toUriString(),
                        HttpMethod.POST,
                        entity,
                        new ParameterizedTypeReference<>() {
                        }
                );
                return response.getBody();
            } catch (Exception e) {
                log.error(e.getMessage(), e);
                throw new XtraceException(localizationService.localize(MessageConstants.ASL_BELGI_XTRACE_ERROR), e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
            }
        }, throwable -> {
            log.error(throwable.getMessage(), throwable);
            throw new IntegrationException(throwable.getMessage());
        });
    }

    public CisPrivateInfoResponse getPrivateInfoByCode(String code, String token) {
        CircuitBreaker cb = circuitBreakerFactory.create(getIntegrationType().name());
        return cb.run(() -> {
            UriComponentsBuilder url = UriComponentsBuilder.fromHttpUrl(applicationProperties.getAslBelgiConfig().getPrivateCheckInfoUrl());
            HttpHeaders headers = SecurityUtils.getHeader();
            headers.setBearerAuth(token);

            HttpEntity<?> entity = new HttpEntity<>(new CheckInfoRequest(List.of(code)), headers);
            ResponseEntity<String> response;
            try {
                response = aslBelgiRestTemplate.exchange(
                        url.toUriString(),
                        HttpMethod.POST,
                        entity,
                        String.class
                );
            } catch (Exception e) {
                log.error("Private codes request failed, code={}", code, e);
                throw new BadRequestException("Xtrace error");
            }

            if (!response.getStatusCode().is2xxSuccessful() || response.getBody() == null) {
                throw new BadRequestException("Xtrace error");
            }

            try {
                CisPrivateInfoResponse privateResponse = objectMapper.readValue(response.getBody(), CisPrivateInfoResponse.class);
                if (privateResponse == null || CollectionUtils.isEmpty(privateResponse.getResults())) {
                    throw new BadRequestException("Xtrace error");
                }
                return privateResponse;
            } catch (BadRequestException e) {
                throw e;
            } catch (Exception e) {
                log.error("Failed to parse private codes response, code={}", code, e);
                throw new BadRequestException("Xtrace error");
            }
        }, throwable -> {
            log.error(throwable.getMessage(), throwable);
            throw new IntegrationException(throwable.getMessage());
        });
    }
}
