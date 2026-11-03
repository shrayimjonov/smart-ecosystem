package uz.uzkassa.cts.integrations;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import uz.uzkassa.cts.config.ApplicationProperties;
import uz.uzkassa.cts.enums.IntegrationType;
import uz.uzkassa.cts.service.TokenStoreService;

@Component
public abstract class BaseClient {

    @Autowired
    protected ApplicationProperties applicationProperties;

    @Autowired
    protected ObjectMapper objectMapper;

    @Autowired
    protected RestTemplate restTemplate;

    @Autowired
    protected TokenStoreService tokenStoreService;

    public abstract String getToken();

    public abstract HttpHeaders getHeaders();

    public abstract IntegrationType getIntegrationType();

    public abstract String authenticate();
}
