package uz.uzkassa.silen.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.hc.core5.http.NameValuePair;
import org.apache.hc.core5.http.message.BasicNameValuePair;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Lazy;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import uz.uzkassa.silen.config.httpclient.CustomRequest;
import uz.uzkassa.silen.config.httpclient.CustomResponse;
import uz.uzkassa.silen.domain.FacturaHost;
import uz.uzkassa.silen.dto.filter.FacturaInvestmentFilter;
import uz.uzkassa.silen.dto.invoice.*;
import uz.uzkassa.silen.dto.vm.SignVM;
import uz.uzkassa.silen.dto.waybill.CompanyEmpowermentsDTO;
import uz.uzkassa.silen.dto.waybill.CompanyTransportDTO;
import uz.uzkassa.silen.dto.waybill.WaybillSendDTO;
import uz.uzkassa.silen.enumeration.Status;
import uz.uzkassa.silen.exceptions.BadRequestException;
import uz.uzkassa.silen.exceptions.EntityNotFoundException;
import uz.uzkassa.silen.repository.pg.FacturaHostRepository;
import uz.uzkassa.silen.service.TelegramBotApi;

import java.io.IOException;
import java.util.*;

@Component
@Slf4j
@Lazy
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class FacturaClient extends BaseIntegrationClient {
    String password;
    String userName;
    FacturaHostRepository facturaHostRepository;
    TelegramBotApi botApi;

    public FacturaClient(final CustomRequest customRequest,
                         final ObjectMapper objectMapper,
                         final TelegramBotApi botApi,
                         @Value("${application.factura.url}") final String startEndpoint,
                         @Value("${application.factura.password}") final String password,
                         @Value("${application.factura.username}") final String userName,
                         final FacturaHostRepository facturaHostRepository) {
        super(customRequest, objectMapper, startEndpoint);
        this.password = password;
        this.userName = userName;
        this.facturaHostRepository = facturaHostRepository;
        this.botApi = botApi;
    }

    @Override
    protected String getBaseUrl() {
        return facturaHostRepository.findFirstByActiveIsTrue().map(FacturaHost::getHost)
                .map(s -> StringUtils.join(s, startEndpoint))
                .orElse("https://factura.yt.uz/provider/api/");
    }

    @Override
    public <T> T convertToResponse(final CustomResponse customResponse, final Class<T> toDto) {
        final String errorMessage = StringUtils.isNotEmpty(customResponse.getErrorMessage()) ? customResponse.getErrorMessage() : customResponse.getContent();
        switch (HttpStatus.valueOf(customResponse.getCode())) {
            case HttpStatus.OK -> {
                try {
                    return objectMapper.readValue(customResponse.getContent(), toDto);
                } catch (final IOException e) {
                    log.error("Json parse error: {}", e.getMessage());
                }
            }
            case HttpStatus.CREATED -> {
                if (customResponse.getContent() == null) return null;
                return objectMapper.convertValue(customResponse.getContent(), toDto);
            }
            case NOT_FOUND -> throw new EntityNotFoundException(errorMessage);
            case BAD_REQUEST, INTERNAL_SERVER_ERROR -> throw new BadRequestException(errorMessage);
            default -> {
                log.error("GET response convert by TIN response: {}", customResponse);
                throw new BadRequestException(parseNicError(customResponse));
            }
        }
        return null;
    }

    public List<LgotaDTO> getLgota(final String mxikCode) {
        final String url = StringUtils.join(getBaseUrl(), "ru/utils/Lgota/without-vat-products?TasnifCode=", mxikCode);
        final CustomResponse response = customRequest.sendGetRequest(url, evaluateHeaders(null));
        final LgotaResponseDTO responseDTO = convertToResponse(response, LgotaResponseDTO.class);
        if (responseDTO != null) {
            return responseDTO.getRows();
        }
        return null;
    }

    public LgotaResponseDTO getLgotaByTin(final String tin) {
        final String url = StringUtils.join(getBaseUrl(), "ru/utils/Lgota/privileged-seller-companies?Tin=", tin);
        final CustomResponse response = customRequest.sendGetRequest(url, evaluateHeaders(null));
        return convertToResponse(response, LgotaResponseDTO.class);
    }

    public ResponseDTO getGuid() {
        final String url = StringUtils.join(getBaseUrl(), "ru/utils/guid");
        final CustomResponse response = customRequest.sendGetRequest(url, evaluateHeaders(null));
        return convertToResponse(response, ResponseDTO.class);
    }

    public ProviderBindingDTO getProviders(final String tin) {
        final String url = StringUtils.join(getBaseUrl(), "ru/register/providerbinding/", tin);

        final CustomResponse response = customRequest.sendGetRequest(url, evaluateHeaders(null));
        return convertToResponse(response, ProviderBindingDTO.class);
    }

    public void saveProviders(final SignVM sign) {
        final String url = StringUtils.join(getBaseUrl(), "ru/register/providerbinding");

        final CustomResponse response = customRequest.sendPostRequest(url, evaluateHeaders(null), convertBodyToJsonString(sign));
        if (HttpStatus.OK.value() != response.getCode()) {
            log.error("POST Save providers response: {}", response);
            throw new BadRequestException(parseNicError(response));
        }
    }

    public RegionDTO[] getRegions() {
        final String url = StringUtils.join(getBaseUrl(), "ru", "/waybills-v2/utils/catalogs/regions");

        final CustomResponse response = customRequest.sendGetRequest(url, evaluateHeaders(null));
        return convertToResponse(response, RegionDTO[].class);
    }

    public DistrictDTO[] getDistricts(Long regionId) {
        final String url = StringUtils.join(getBaseUrl(), "ru", "/waybills-v2/utils/catalogs/districts");
        List<NameValuePair> params = List.of(new BasicNameValuePair("regionId", regionId.toString()));
        final CustomResponse response = customRequest.sendGetRequest(url, evaluateHeaders(null), params);
        return convertToResponse(response, DistrictDTO[].class);
    }

    public VillageDTO[] getVillages(String soato) {
        final String url = StringUtils.join(getBaseUrl(), "ru", "/waybills-v2/utils/catalogs/villages");
        List<NameValuePair> params = List.of(new BasicNameValuePair("soato", soato));
        final CustomResponse response = customRequest.sendGetRequest(url, evaluateHeaders(null), params);
        return convertToResponse(response, VillageDTO[].class);
    }

    public MeasureDTO[] getMeasures(final String lang) {
        final String url = StringUtils.join(getBaseUrl(), lang, "/catalogs/measure");

        final CustomResponse response = customRequest.sendGetRequest(url, evaluateHeaders(null));
        return convertToResponse(response, MeasureDTO[].class);
    }

    public BankDTO[] getBanks(final String lang) {
        final String url = StringUtils.join(getBaseUrl(), lang, "/catalogs/bank");
        final CustomResponse response = customRequest.sendGetRequest(url, evaluateHeaders(null));
        return convertToResponse(response, BankDTO[].class);
    }

    public void sendFactura(final String lang, final String tin, final FacturaSendDTO dto) {
        long start = System.currentTimeMillis();
        final String url = StringUtils.join(getBaseUrl(), lang, "/", tin, "/facturas/seller");

        final CustomResponse response = customRequest.sendPostRequest(url, evaluateHeaders(null), convertBodyToJsonString(dto));
        if (HttpStatus.OK.value() == response.getCode()) {
            log.info("SEND FACTURA :{} ============== TIME: {}ms", dto.getFacturaId(), (System.currentTimeMillis() - start));
        } else {
            StringBuilder message = new StringBuilder("Type: ")
                    .append("FACTURA ID:").append(dto.getFacturaId()).append(" \n")
                    .append("SELLER TIN: ").append(tin).append(" \n")
                    .append("CLIENT IP: ").append(dto.getClientIp()).append(" \n")
                    .append("URL: ").append(url).append(" \n")
                    .append("RESPONSE: ").append(" \n")
                    .append(response);
            botApi.sendMessageToAdminChannel(message.toString());
            log.error("POST Send factura response: {}", response);
            throw new BadRequestException(parseNicError(response));
        }
    }

    public void cancelFactura(final String lang, final String tin, final FacturaSendDTO facturaSendDTO) {
        final String url = StringUtils.join(getBaseUrl(), lang, "/", tin, "/facturas/seller/cancel");

        final CustomResponse response = customRequest.sendPostRequest(url, evaluateHeaders(null), convertBodyToJsonString(facturaSendDTO));
        if (HttpStatus.OK.value() != response.getCode()) {
            log.error("POST Cancel factura response: {}", response);
            throw new BadRequestException(parseNicError(response));
        }
    }

    public void acceptOrReject(final String lang, final String tin, final FacturaSendDTO facturaSendDTO) {
        final String url = StringUtils.join(getBaseUrl(), lang, "/", tin, "/facturas/buyer");

        final CustomResponse response = customRequest.sendPostRequest(url, evaluateHeaders(null), convertBodyToJsonString(facturaSendDTO));
        if (HttpStatus.OK.value() != response.getCode()) {
            log.error("POST {} factura response: {} ", facturaSendDTO.getAction(), response);
            throw new BadRequestException(parseNicError(response));
        }
    }

    public FacturaDTO getSellerFactura(final String tin, final String facturaId) {
        final String url = StringUtils.join(getBaseUrl(), "ru/", tin, "/facturas/seller/", facturaId);
        final CustomResponse response = customRequest.sendGetRequest(url, evaluateHeaders(null));
        return convertToResponse(response, FacturaDTO.class);
    }

    public String getSellerFacturaSign(final String tin, final String facturaId, final Status status) {
        final String url = StringUtils.join(getBaseUrl(), "ru/", tin, "/facturas/seller/signedfile/", facturaId, "?status=", status.getNicStatus());
        final CustomResponse response = customRequest.sendGetRequest(url, evaluateHeaders(null));
        if (HttpStatus.OK.value() == response.getCode()) {
            return response.getContent();
        }
        return convertToResponse(response, String.class);
    }

    public String getBuyerFacturaSign(final String tin, final String facturaId, final Status status) {
        final String url = StringUtils.join(getBaseUrl(), "ru/", tin, "/facturas/buyer/signedfile/", facturaId, "?status=", status.getNicStatus());
        final CustomResponse response = customRequest.sendGetRequest(url, evaluateHeaders(null));
        if (HttpStatus.OK.value() == response.getCode()) {
            return response.getContent();
        }
        return convertToResponse(response, String.class);
    }

    @Override
    protected Map<String, String> evaluateHeaders(final String token) {
        final Map<String, String> headers = new HashMap<>();
        final String credentials = Base64.getEncoder().encodeToString(StringUtils.join(userName, ":", password).getBytes());
        headers.put(HttpHeaders.AUTHORIZATION, StringUtils.join("Basic ", credentials));
        return headers;
    }

    private String parseNicError(final CustomResponse response) {
        if (StringUtils.isNotBlank(response.getErrorMessage())) {
            return response.getErrorMessage();
        }
        try {
            final FacturaErrorResponse responseDTO = objectMapper.readValue(response.getContent(), FacturaErrorResponse.class);
            return Optional.ofNullable(responseDTO)
                    .map(FacturaErrorResponse::getErrorMessage)
                    .orElseThrow(() -> new BadRequestException("Непредвиденная ошибка"));
        } catch (IOException e) {
            return response.getContent();
        }
    }

    public void sendWaybill(final String lang, final String tin, final WaybillSendDTO dto, final UUID requestId, final String id) {
        long start = System.currentTimeMillis();
        String url = StringUtils.join(getBaseUrl(), lang, "/", tin, "/waybills-v2/local/consignor");

        final CustomResponse response = customRequest.sendPostRequest(url, evaluateHeaders(null), convertBodyToJsonString(dto));
        if (HttpStatus.OK.value() == response.getCode()) {
            log.info("SEND WAYBILL :{} ============== TIME: {}ms", id, (System.currentTimeMillis() - start));
        } else {
            StringBuilder message = new StringBuilder("Type: ")
                    .append("REQUEST ID:").append(requestId).append(" \n")
                    .append("WAYBILL ID:").append(id).append(" \n")
                    .append("SELLER TIN: ").append(tin).append(" \n")
                    .append("CLIENT IP: ").append(dto.getClientIp()).append(" \n")
                    .append("URL: ").append(url).append(" \n")
                    .append("RESPONSE: ").append(" \n")
                    .append(response);
            botApi.sendMessageToAdminChannel(message.toString());
            log.error("POST Send waybill response: {}", response);
            throw new BadRequestException(parseNicError(response));
        }
    }

    public void cancelWaybill(final String lang, final String tin, final WaybillSendDTO waybillSendDTO, final String id) {
        final String url = StringUtils.join(getBaseUrl(), lang, "/", tin, "/waybills-v2/local/consignor/cancel");

        final CustomResponse response = customRequest.sendPostRequest(url, evaluateHeaders(null), convertBodyToJsonString(waybillSendDTO));
        if (HttpStatus.OK.value() != response.getCode()) {
            StringBuilder message = new StringBuilder("Type: ")
                    .append("REQUEST: ").append(waybillSendDTO).append(" \n")
                    .append("WAYBILL ID:").append(id).append(" \n")
                    .append("SELLER TIN: ").append(tin).append(" \n")
                    .append("CLIENT IP: ").append(waybillSendDTO.getClientIp()).append(" \n")
                    .append("URL: ").append(url).append(" \n")
                    .append("RESPONSE: ").append(" \n")
                    .append(response);
            botApi.sendMessageToAdminChannel(message.toString());
            log.error("POST Cancel waybill response: {}", response);
            throw new BadRequestException(parseNicError(response));
        }
    }

    public void acceptWaybill(final String lang, final String tin, final WaybillSendDTO waybillSendDTO, final String id) {
        final String url = StringUtils.join(getBaseUrl(), lang, "/", tin, "/waybills-v2/local/consignee");

        final CustomResponse response = customRequest.sendPostRequest(url, evaluateHeaders(null), convertBodyToJsonString(waybillSendDTO));
        if (HttpStatus.OK.value() != response.getCode()) {
            StringBuilder message = new StringBuilder("Type: ")
                    .append("REQUEST: ").append(waybillSendDTO).append(" \n")
                    .append("WAYBILL ID:").append(id).append(" \n")
                    .append("SELLER TIN: ").append(tin).append(" \n")
                    .append("CLIENT IP: ").append(waybillSendDTO.getClientIp()).append(" \n")
                    .append("URL: ").append(url).append(" \n")
                    .append("RESPONSE: ").append(" \n")
                    .append(response);
            botApi.sendMessageToAdminChannel(message.toString());
            log.error("POST Cancel waybill response: {}", response);
            throw new BadRequestException(parseNicError(response));
        }
    }

    public void rejectWaybill(final String lang, final String tin, final WaybillSendDTO waybillSendDTO, final String id) {
        final String url = StringUtils.join(getBaseUrl(), lang, "/", tin, "/waybills-v2/local/consignee/reject");

        final CustomResponse response = customRequest.sendPostRequest(url, evaluateHeaders(null), convertBodyToJsonString(waybillSendDTO));
        if (HttpStatus.OK.value() != response.getCode()) {
            StringBuilder message = new StringBuilder("Type: ")
                    .append("REQUEST: ").append(waybillSendDTO).append(" \n")
                    .append("WAYBILL ID:").append(id).append(" \n")
                    .append("SELLER TIN: ").append(tin).append(" \n")
                    .append("CLIENT IP: ").append(waybillSendDTO.getClientIp()).append(" \n")
                    .append("URL: ").append(url).append(" \n")
                    .append("RESPONSE: ").append(" \n")
                    .append(response);
            botApi.sendMessageToAdminChannel(message.toString());
            log.error("POST Cancel waybill response: {}", response);

            throw new BadRequestException(parseNicError(response));
        }
    }

    public CompanyTransportDTO[] getCompanyTransports(String tin) {
        final String url = getBaseUrl() + "ru/waybills-v2/utils/transport?tinOrPinfl=" + tin;
        final CustomResponse response = customRequest.sendGetRequest(url, evaluateHeaders(null));
        return convertToResponse(response, CompanyTransportDTO[].class);
    }

    /**
     * 6 - отправлено дов.лицу;
     * 7 - доставлено дов.лицу, ожидает его подписи;
     * 10 - отправлено поставщику;
     * 15 - доставлено поставщику, ожидает его подписи.
     *
     * @param tin
     * @return
     */
    public CompanyEmpowermentsDTO getCompanyEmpowerments(String tin) {
        final String url = getBaseUrl() + "ru/" + tin + "/empowerments/seller?stateId=15";

        final CustomResponse response = customRequest.sendGetRequest(url, evaluateHeaders(null));
        return convertToResponse(response, CompanyEmpowermentsDTO.class);
    }

    /**
     * [
     * {
     * "stationId": "730604",
     * "name": "KAPAKУЛЬ"
     * },
     * {
     * "stationId": "723827",
     * "name": "УЗБEKИCTAH"
     * }
     * ]
     *
     * @return
     */
    public Object getStations() {
        final String url = getBaseUrl() + "ru/" + "/catalogs/station";

        final CustomResponse response = customRequest.sendGetRequest(url, evaluateHeaders(null));
        return convertToResponse(response, Object.class);
    }

    public Object getInvestmentObject(FacturaInvestmentFilter filter) {
        final String url = getBaseUrl() + "ru/" + "/utils/investment-object";
        final CustomResponse response = customRequest.sendGetRequest(url, evaluateHeaders(null), filter.convertToParams());
        return convertToResponse(response, Object.class);
    }

    public Object checkInvestment(String tin) {
        final String url = getBaseUrl() + "ru/" + "/utils/investment-object/"+tin;
        final CustomResponse response = customRequest.sendGetRequest(url, evaluateHeaders(null));
        return convertToResponse(response, Object.class);
    }
}
