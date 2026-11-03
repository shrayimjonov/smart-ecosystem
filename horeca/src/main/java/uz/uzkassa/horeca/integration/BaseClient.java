package uz.uzkassa.horeca.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;
import uz.uzkassa.horeca.config.ApplicationProperties;
import uz.uzkassa.horeca.service.LocalizationService;
import uz.uzkassa.horeca.web.rest.errors.IntegrationException;

@Component
public class BaseClient {

    @Autowired
    protected ApplicationProperties applicationProperties;

    @Autowired
    protected ObjectMapper objectMapper;

    @Autowired
    protected LocalizationService localizationService;

    @Autowired
    protected RestTemplate restTemplate;


    protected <T> T commonExchange(UriComponentsBuilder uriBuilder, HttpMethod method, @Nullable HttpEntity<?> httpEntity, Class<T> responseType) {
        try {
            ResponseEntity<T> response = restTemplate.exchange(uriBuilder.toUriString(), method, httpEntity, responseType);
            if (HttpStatus.OK == response.getStatusCode()) {
                return objectMapper.convertValue(response.getBody(), responseType);
            } else {
                throw new IntegrationException(response.toString());
            }
        } catch (Exception e) {
            throw new IntegrationException("Fiscal check from Cts error", e.getMessage());
        }
    }

    protected <R, T> T post(UriComponentsBuilder uriBuilder, HttpEntity<R> httpEntity, Class<T> responseType) {
        return this.commonExchange(uriBuilder, HttpMethod.POST, httpEntity, responseType);
    }

    protected <R, T> T put(UriComponentsBuilder uriBuilder, HttpEntity<R> httpEntity, Class<T> responseType) {
        return this.commonExchange(uriBuilder, HttpMethod.PUT, httpEntity, responseType);
    }

    protected <R, T> T get(UriComponentsBuilder uriBuilder, HttpEntity<R> httpEntity, Class<T> responseType) {
        return this.commonExchange(uriBuilder, HttpMethod.GET, httpEntity, responseType);
    }
}
