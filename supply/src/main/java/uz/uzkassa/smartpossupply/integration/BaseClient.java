package uz.uzkassa.smartpossupply.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.client.circuitbreaker.CircuitBreakerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import uz.uzkassa.smartpossupply.config.ApplicationProperties;
import uz.uzkassa.smartpossupply.config.httpclient.CustomRequest;
import uz.uzkassa.smartpossupply.service.LocalizationService;

@Component
public class BaseClient {

    @Autowired
    @SuppressWarnings("rawtypes")
    protected CircuitBreakerFactory circuitBreakerFactory;

    @Autowired
    protected ApplicationProperties applicationProperties;

    @Autowired
    protected ObjectMapper objectMapper;

    @Autowired
    protected RestTemplate restTemplate;

    @Autowired
    protected CustomRequest customRequest;

    @Autowired
    protected LocalizationService localizationService;

    public IntegrationType getIntegrationType() {
        return null;
    }

}
