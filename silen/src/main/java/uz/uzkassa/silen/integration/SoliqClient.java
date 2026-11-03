package uz.uzkassa.silen.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
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
import uz.uzkassa.silen.dto.SelectItem;
import uz.uzkassa.silen.dto.invoice.TINAbstractDTO;
import uz.uzkassa.silen.dto.invoice.VATCompanyDateDTO;
import uz.uzkassa.silen.dto.waybill.NicBranchDTO;
import uz.uzkassa.silen.dto.waybill.PhysicalPersonDTO;
import uz.uzkassa.silen.exceptions.BadRequestException;
import uz.uzkassa.silen.repository.pg.FacturaHostRepository;
import uz.uzkassa.silen.service.TelegramBotApi;
import uz.uzkassa.silen.utils.DateUtils;

import java.io.IOException;
import java.time.LocalDate;
import java.util.Base64;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


@Slf4j
@Lazy
@Getter
@Setter
@Component
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class SoliqClient extends BaseIntegrationClient {
    String userName;
    String password;
    FacturaHostRepository facturaHostRepository;

    public SoliqClient(final CustomRequest customRequest,
                       final ObjectMapper objectMapper,
                       final TelegramBotApi botApi,
                       @Value("${application.soliq.url}") final String startEndpoint,
                       @Value("${application.soliq.username}") final String userName,
                       @Value("${application.soliq.password}") final String password,
                       final FacturaHostRepository facturaHostRepository) {
        super(customRequest, objectMapper, startEndpoint);
        this.userName = userName;
        this.password = password;
        this.facturaHostRepository = facturaHostRepository;
    }

    @Override
    protected String getBaseUrl() {
        return facturaHostRepository.findFirstByActiveIsTrue().map(FacturaHost::getHost)
                .map(s -> StringUtils.join(s, startEndpoint))
                .orElse("https://factura.yt.uz/mysoliq/services/");
    }

    @Override
    public <T> T convertToResponse(final CustomResponse customResponse, final Class<T> toDto) {
        final String errorMessage = StringUtils.isNotEmpty(customResponse.getErrorMessage()) ? customResponse.getErrorMessage() : customResponse.getContent();
        switch (HttpStatus.valueOf(customResponse.getCode())) {
            case HttpStatus.OK -> {
                try {
                    return objectMapper.readValue(customResponse.getContent(), toDto);
                } catch (final IOException e) {
                    log.error("Response convert to JSON error: {}", e.getMessage());
                }
            }
            default -> throw new BadRequestException(errorMessage);
        }
        return null;
    }

    public VATCompanyDateDTO getCompanyVatByDate(String tin) {
        String date = DateUtils.format(LocalDate.now());
        final String url = getBaseUrl() + "nds/reference/status?" + (tin.length() > 9 ? "pinfl=" : "tin=") + tin + "&facturaDate=" + date;

        final CustomResponse response = customRequest.sendGetRequest(url, evaluateHeaders(null));
        return convertToResponse(response, VATCompanyDateDTO.class);
    }

    public TINAbstractDTO getCustomerByTin(String tin) {
        final String url = getBaseUrl() + "np1/bytin/factura-all?lang=uz&" + (tin.length() > 9 ? "pinfl=" : "tin=") + tin;

        final CustomResponse response = customRequest.sendGetRequest(url, evaluateHeaders(null));
        return convertToResponse(response, TINAbstractDTO.class);
    }

    public SelectItem[] getCompanyByUser(String lang, String tin) {
        lang = StringUtils.isNotEmpty(lang) ? lang : "ru";
        final String url = getBaseUrl() + "np1/by-directortin/factura?lang=" + lang + "&tin=" + tin;
        List<NameValuePair> params = List.of(new BasicNameValuePair("stateId", "15"));
        final CustomResponse response = customRequest.sendGetRequest(url, evaluateHeaders(null), params);
        return convertToResponse(response, SelectItem[].class);
    }

    public NicBranchDTO[] getBranchesByCompany(String tin) {
        final String url = getBaseUrl() + "yur-branchs/getdatabytin?tin=" + tin;
        final CustomResponse response = customRequest.sendGetRequest(url, evaluateHeaders(null));
        return convertToResponse(response, NicBranchDTO[].class);
    }

    public PhysicalPersonDTO getUserByTinOrPinfl(String tinOrPinfl) {
        StringBuilder url = new StringBuilder();
        url.append(this.getBaseUrl());
        url.append("np1/phisbytin/factura?lang=ru&");
        if (tinOrPinfl.length() > 9) {
            url.append("pinfl=");
        } else {
            url.append("tin=");
        }
        url.append(tinOrPinfl);
        final CustomResponse response = customRequest.sendGetRequest(url.toString(), evaluateHeaders(null));
        return convertToResponse(response, PhysicalPersonDTO.class);
    }

    @Override
    protected Map<String, String> evaluateHeaders(String token) {
        Map<String, String> headers = new HashMap<>();
        final String credentials = Base64.getEncoder().encodeToString(StringUtils.join(userName, ":", password).getBytes());
        headers.put(HttpHeaders.AUTHORIZATION, "Basic " + credentials);
        return headers;
    }


}
