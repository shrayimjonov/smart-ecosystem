package uz.uzkassa.cts.service.impl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import uz.uzkassa.cts.config.ApplicationProperties;
import uz.uzkassa.cts.dto.kapitallegal.request.KapitalLegalGetDocInfoRequest;
import uz.uzkassa.cts.dto.kapitallegal.request.KapitalLegalGetDocsRequest;
import uz.uzkassa.cts.dto.kapitallegal.request.KapitalLegalPaymentRequest;
import uz.uzkassa.cts.dto.kapitallegal.response.*;
import uz.uzkassa.cts.service.KapitalBankLegalService;
import uz.uzkassa.cts.web.rest.errors.IntegrationException;

import java.util.Base64;

import static uz.uzkassa.cts.security.SecurityUtils.getHeader;

@Slf4j
@Service
public class KapitalBankLegalServiceImpl implements KapitalBankLegalService {

    private final RestTemplate restTemplate;
    private final ApplicationProperties applicationProperties;

    public KapitalBankLegalServiceImpl(@Qualifier("kapitalLegalTemplate") RestTemplate restTemplate, ApplicationProperties applicationProperties) {
        this.restTemplate = restTemplate;
        this.applicationProperties = applicationProperties;
    }

    private HttpHeaders getHttpHeaders() {
        HttpHeaders headers = getHeader();
        String base64auth = Base64.getEncoder()
            .encodeToString((applicationProperties.getKapitalBankLegalProperty().getUsername() + ":" +
                applicationProperties.getKapitalBankLegalProperty().getPassword()).getBytes());
        headers.set("Authorization", "Basic " + base64auth);
        return headers;
    }

    @Override
    public KapitalLegalLoginResponse authenticate() {
        ResponseEntity<KapitalLegalLoginResponse> response =
            restTemplate.exchange(applicationProperties.getKapitalBankLegalProperty().getHost() + "/Mobile.svc/APILogin",
                HttpMethod.POST, new HttpEntity<>(getHttpHeaders()), KapitalLegalLoginResponse.class);

        if (response.getStatusCode() != HttpStatus.OK)
            throw new IntegrationException("Error occurred while authenticating kapital legal :" + response.getStatusCode());
        return response.getBody();
    }

    @Override
    public KapitalLegalDoc1CResponse get1CDocuments(KapitalLegalGetDocsRequest request) {
        return exchange(request,
            applicationProperties.getKapitalBankLegalProperty().getHost() + "/Mobile.svc/GetDoc1C",
            HttpMethod.POST,
            KapitalLegalDoc1CResponse.class);
    }

    @Override
    public KapitalLegalGetDocInfoResponse getAccountInfo(KapitalLegalGetDocInfoRequest request) {
        return exchange(request,
            applicationProperties.getKapitalBankLegalProperty().getHost() + "/Mobile.svc/GetAcc1C",
            HttpMethod.POST,
            KapitalLegalGetDocInfoResponse.class);
    }

    @Override
    public KapitalLegalSendPaymentResponse sendPayment(KapitalLegalPaymentRequest request) {
        return exchange(request,
            applicationProperties.getKapitalBankLegalProperty().getHost() + "/Mobile.svc/SendPayment",
            HttpMethod.POST,
            KapitalLegalSendPaymentResponse.class);
    }

    @Override
    public KapitalLegalCheckPaymentResponse checkPayment(KapitalLegalPaymentRequest request) {
        return exchange(request,
            applicationProperties.getKapitalBankLegalProperty().getHost() + "/Mobile.svc/CheckPayments2",
            HttpMethod.POST,
            KapitalLegalCheckPaymentResponse.class);
    }

    private <T, R> T exchange(R request, String url, HttpMethod method, Class<T> clazz){
        ResponseEntity<T> response = restTemplate.exchange(url, method, new HttpEntity<>(request, getHttpHeaders()), clazz);

        if (response.getStatusCode() != HttpStatus.OK)
            throw new IntegrationException("Error occurred url: " + url + " Status: " + response.getStatusCode());

        return response.getBody();
    }


}
