package uz.uzkassa.smartpos.integration;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.context.annotation.Lazy;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import uz.uzkassa.smartpos.config.ApplicationProperties;
import uz.uzkassa.smartpos.config.httpclient.CustomRequest;
import uz.uzkassa.smartpos.config.httpclient.CustomResponse;
import uz.uzkassa.smartpos.dto.factura.*;
import uz.uzkassa.smartpos.dto.nic.ErrorResponseDTO;
import uz.uzkassa.smartpos.dto.nic.FacturaSendDTO;
import uz.uzkassa.smartpos.dto.nic.SignVM;
import uz.uzkassa.smartpos.dto.nic.TINAbstractDTO;
import uz.uzkassa.smartpos.enums.Status;
import uz.uzkassa.smartpos.exceptions.BadRequestException;
import uz.uzkassa.smartpos.security.HeaderUtils;

import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Component
@Slf4j
@Lazy
@RequiredArgsConstructor
public class FacturaClient {
    private final ApplicationProperties applicationProperties;
    private final CustomRequest customRequest;
    private final ObjectMapper objectMapper;

    public String getGuid() {
        final CustomResponse response = customRequest.sendGetRequest(applicationProperties.getFactura().getHost() +
                "ru/utils/guid", getHeaders(false));
        if (response.getCode() == 200) {
            try {
                final ResponseDTO responseDTO = objectMapper.readValue(response.getContent(), ResponseDTO.class);
                return Optional.ofNullable(responseDTO).map(ResponseDTO::getData).orElse(null);
            } catch (final JsonProcessingException e) {
                log.error("Json parse error: {}", e.getMessage());
            }
        } else {
            log.error("GET Guid response: {}", response);
            throw new BadRequestException(parseNicError(response));
        }
        return null;
    }

    public TINAbstractDTO getCustomerByTin(final String tin) {
        final CustomResponse response = customRequest.sendGetRequest(applicationProperties.getSoliq().getHost() +
                "np1/bytin/factura-all?" + (tin.length() > 9 ? "pinfl=" : "tin=") + tin, getHeaders(true));
        if (response.getCode() == 200) {
            try {
                final TINAbstractDTO result = objectMapper.readValue(response.getContent(), TINAbstractDTO.class);
                if (result != null && StringUtils.isNotEmpty(result.getTin())) {
                    if (StringUtils.isEmpty(result.getName())) {
                        log.debug("CUSTOMER: {}", response.getContent());
                    }
                    return result;
                } else {
                    throw new BadRequestException("Нет данных по данному ИНН: " + tin);
                }
            } catch (final JsonProcessingException e) {
                log.error("Json parse error: {}", e.getMessage());
            }
        } else {
            log.error("GET Company by TIN response: {}", response);
            throw new BadRequestException(parseNicError(response));
        }
        return null;
    }

    public VATCompanyDate getCompanyVatByDate(final String tin) {
        final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy");
        final String date = LocalDate.now().format(formatter);
        final CustomResponse response = customRequest.sendGetRequest(applicationProperties.getSoliq().getHost() +
                "nds/reference/status?" + (tin.length() > 9 ? "pinfl=" : "tin=") + tin + "&facturaDate=" + date, getHeaders(true));
        if (response.getCode() == 200) {
            try {
                return objectMapper.readValue(response.getContent(), VATCompanyDate.class);
            } catch (final JsonProcessingException e) {
                log.error("Json parse error: {}", e.getMessage());
            }
        } else {
            log.error("GET Company Date VAT response: {}", response);
            throw new BadRequestException(parseNicError(response));
        }
        return null;
    }

    public TINBranchDTO[] getBranchesByCompany(final String tin) {
        final CustomResponse response = customRequest.sendGetRequest(applicationProperties.getSoliq().getHost() +
                "yur-branchs/getdatabytin?tin=" + tin, getHeaders(true));
        if (response.getCode() == 200) {
            try {
                return objectMapper.readValue(response.getContent(), TINBranchDTO[].class);
            } catch (final JsonProcessingException e) {
                log.error("Json parse error: {}", e.getMessage());
            }
        } else {
            log.error("GET User by TIN response: {}", response);
            throw new BadRequestException(parseNicError(response));
        }
        return null;
    }

    public void saveProviders(SignVM sign) {
        try {
            final String json = objectMapper.writeValueAsString(sign);
            final CustomResponse response = customRequest.sendPostRequest(applicationProperties.getFactura().getHost() + "ru/register/providerbinding",
                    getHeaders(false), json);
            if (response.getCode() != 200) {
                log.error("POST Save providers response: {}", response);
                throw new BadRequestException(parseNicError(response));
            }
        } catch (final JsonProcessingException e) {
            log.error("Save providers convert to JSON error: {}", e.getMessage());
            throw new BadRequestException(e.getMessage());
        }
    }


    public List<LgotaDTO> getLgotaByTin(String lang, final String tin) {
        lang = StringUtils.isNotEmpty(lang) ? lang : "ru";
        final String url = applicationProperties.getFactura().getHost() + lang + "/utils/Lgota/privileged-seller-companies?Tin=" + tin;
        final CustomResponse response = customRequest.sendGetRequest(url, getHeaders(false));
        if (response.getCode() == 200) {
            try {
                final LgotaResponseDTO responseDTO = objectMapper.readValue(response.getContent(), LgotaResponseDTO.class);
                if (responseDTO != null) {
                    return responseDTO.getRows();
                }
            } catch (final JsonProcessingException e) {
                log.error("Json parse error: {}", e.getMessage());
            }
        } else {
            log.error("GET Lgota by TIN response: {}", response);
            throw new BadRequestException(parseNicError(response));
        }
        return new ArrayList<>();
    }

    public List<LgotaDTO> getLgota(String lang, final String mxikCode) {
        lang = StringUtils.isNotEmpty(lang) ? lang : "ru";
        final CustomResponse response = customRequest.sendGetRequest(applicationProperties.getFactura().getHost() + lang +
                "/utils/Lgota/without-vat-products?TasnifCode=" + mxikCode, getHeaders(false));
        if (response.getCode() == 200) {
            try {
                LgotaResponseDTO responseDTO = objectMapper.readValue(response.getContent(), LgotaResponseDTO.class);
                if (responseDTO != null) {
                    return responseDTO.getRows();
                }
            } catch (final JsonProcessingException e) {
                log.error("Json parse error: {}", e.getMessage());
            }
        } else {
            log.error("GET Lgota by TIN response: {}", response);
            throw new BadRequestException(parseNicError(response));
        }
        return new ArrayList<>();
    }

    public FacturaBankDTO[] getBanks(String lang) {
        lang = StringUtils.isNotEmpty(lang) ? lang : "ru";
        final CustomResponse response = customRequest.sendGetRequest(applicationProperties.getFactura().getHost() +
                lang + "/catalogs/bank", getHeaders(false));
        if (response.getCode() == 200) {
            try {
                return objectMapper.readValue(response.getContent(), FacturaBankDTO[].class);
            } catch (final JsonProcessingException e) {
                log.error("Json parse error: {}", e.getMessage());
            }
        } else {
            log.error("GET Banks response: {}", response);
        }
        return null;
    }

    public void sendFactura(String lang, final String tin, final FacturaSendDTO facturaSendDTO) {
        long start = System.currentTimeMillis();
        lang = StringUtils.isNotEmpty(lang) ? lang : "ru";
        try {
            facturaSendDTO.setClientIp(HeaderUtils.getRealIP());
            final String json = objectMapper.writeValueAsString(facturaSendDTO);
            final CustomResponse response = customRequest.sendPostRequest(applicationProperties.getFactura().getHost() + lang + "/" + tin + "/facturas/seller",
                    getHeaders(false), json);
            if (response.getCode() == 200) {
                log.info("SEND FACTURA :{} ============== TIME: {}ms", facturaSendDTO.getFacturaId(), (System.currentTimeMillis() - start));
            } else {
                log.error("POST Send factura response: {}", response);
                throw new BadRequestException(parseNicError(response));
            }
        } catch (final JsonProcessingException e) {
            log.error("Send factura convert to JSON error: {}", e.getMessage());
            throw new BadRequestException(e.getMessage());
        }
    }

    public void cancelFactura(final String tin, final FacturaSendDTO facturaSendDTO) {
        try {
            facturaSendDTO.setClientIp(HeaderUtils.getRealIP());
            final String json = objectMapper.writeValueAsString(facturaSendDTO);
            final CustomResponse response = customRequest.sendPostRequest(applicationProperties.getFactura().getHost() + "ru/" + tin + "/facturas/seller/cancel",
                    getHeaders(false), json);
            if (HttpStatus.OK.value() != response.getCode()) {
                log.error("POST Cancel factura response: {}", response);
                throw new BadRequestException(parseNicError(response));
            }
        } catch (final JsonProcessingException e) {
            log.error("Cancel factura convert to JSON error: {}", e.getMessage());
            throw new BadRequestException(e.getMessage());
        }
    }

    public void acceptOrReject(final String tin, final FacturaSendDTO facturaSendDTO) {
        try {
            facturaSendDTO.setClientIp(HeaderUtils.getRealIP());
            final String json = objectMapper.writeValueAsString(facturaSendDTO);
            final CustomResponse response = customRequest.sendPostRequest(applicationProperties.getFactura().getHost() + "ru/" + tin + "/facturas/buyer",
                    getHeaders(false), json);
            if (response.getCode() != 200) {
                log.error("POST {} factura response: {} ", facturaSendDTO.getAction(), response);
                throw new BadRequestException(parseNicError(response));
            }
        } catch (JsonProcessingException e) {
            log.error("{} factura convert to JSON error: {}", facturaSendDTO.getAction(), e.getMessage());
            throw new BadRequestException(e.getMessage());
        }
    }

    public FacturaDTO getSellerFactura(final String tin, final String facturaId) {
        final CustomResponse response = customRequest.sendGetRequest(applicationProperties.getFactura().getHost() + "ru/" + tin + "/facturas/seller/" + facturaId,
                getHeaders(false));
        if (response.getCode() == 200) {
            try {
                log.info("CONTENT: {}", response.getContent());
                return objectMapper.readValue(response.getContent(), FacturaDTO.class);
            } catch (final JsonProcessingException e) {
                log.error("Json parse error: {}", e.getMessage());
            }
        } else {
            log.error("GET Factura response: {}", response);
        }
        return null;
    }

    public String getSellerFacturaSign(final String tin, final String facturaId, final Status status) {
        final String facturaStatus = status.getFacturaStatus();
        final CustomResponse response = customRequest.sendGetRequest(applicationProperties.getFactura().getHost() + "ru/"
                        + tin + "/facturas/seller/signedfile/" + facturaId + "?status=" + facturaStatus,
                getHeaders(false));
        if (response.getCode() == 200) {
            return response.getContent();
        } else {
            return null;
        }
    }

    public FacturaDTO getBuyerFactura(final String tin, final String facturaId) {
        final CustomResponse response = customRequest.sendGetRequest(applicationProperties.getFactura().getHost() + "ru/"
                        + tin + "/facturas/buyer/" + facturaId,
                getHeaders(false));
        if (response.getCode() == 200) {
            try {
                return objectMapper.readValue(response.getContent(), FacturaDTO.class);
            } catch (final JsonProcessingException e) {
                log.error("Json parse error: {}", e.getMessage());
            }
        } else {
            log.error("GET Factura response: {}", response);
        }
        return null;
    }

    public String getBuyerFacturaSign(final String tin, final String facturaId, final Status status, boolean throwException) {
        final String facturaStatus = status.getFacturaStatus();
        final CustomResponse response = customRequest.sendGetRequest(applicationProperties.getFactura().getHost() + "ru/"
                        + tin + "/facturas/buyer/signedfile/" + facturaId + "?status=" + facturaStatus,
                getHeaders(false));
        if (response.getCode() == 200) {
            return response.getContent();
        } else if (throwException) {
            throw new BadRequestException(parseNicError(response));
        }
        return null;
    }

    private String parseNicError(final CustomResponse response) {
        if (StringUtils.isNotBlank(response.getErrorMessage())) {
            return response.getErrorMessage();
        }
        try {
            final ErrorResponseDTO responseDTO = objectMapper.readValue(response.getContent(), ErrorResponseDTO.class);
            return Optional.ofNullable(responseDTO)
                    .map(ErrorResponseDTO::getErrorMessage)
                    .orElseThrow(() -> new BadRequestException("Непредвиденная ошибка"));
        } catch (IOException e) {
            return response.getContent();
        }
    }

    private Map<String, String> getHeaders(final boolean isSoliq) {
        final Map<String, String> headers = new HashMap<>();
        String credentials;
        if (isSoliq) {
            credentials = Base64.getEncoder().encodeToString((applicationProperties.getSoliq().getUsername() + ":" + applicationProperties.getSoliq().getPassword()).getBytes());
        } else {
            credentials = Base64.getEncoder().encodeToString((applicationProperties.getFactura().getUsername() + ":" + applicationProperties.getFactura().getPassword()).getBytes());
        }
        headers.put(HttpHeaders.AUTHORIZATION, "Basic " + credentials);
        return headers;
    }
}
