package uz.uzkassa.smartpos.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.hc.client5.http.classic.HttpClient;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.web.client.ResponseErrorHandler;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.DefaultUriBuilderFactory;
import uz.uzkassa.smartpos.exceptions.BadRequestException;
import uz.uzkassa.smartpos.interceptor.RestTemplateLoggingInterceptor;
import uz.uzkassa.smartpos.interceptor.RestTemplateResponseErrorHandler;
import uz.uzkassa.smartpos.service.LocalizationService;

import java.io.IOException;
import java.util.Map;


@Configuration(proxyBeanMethods = false)
@Slf4j
public class RestClientConfig {

    @Bean
    @Primary
    public RestTemplate restTemplate() {
        SimpleClientHttpRequestFactory factory = new SimpleClientHttpRequestFactory();
        factory.setConnectTimeout(10000);
        factory.setReadTimeout(30000);
        RestTemplate restTemplate = new RestTemplate(factory);
        restTemplate.getInterceptors().add(new RestTemplateLoggingInterceptor());
        restTemplate.setErrorHandler(new RestTemplateResponseErrorHandler());
        return restTemplate;
    }

    @Bean("socketRestTemplate")
    public RestTemplate socketRestTemplate() {
        SimpleClientHttpRequestFactory factory = new SimpleClientHttpRequestFactory();
        factory.setConnectTimeout(10000);
        factory.setReadTimeout(30000);
        RestTemplate restTemplate = new RestTemplate(factory);
        restTemplate.setErrorHandler(new RestTemplateResponseErrorHandler());
        return restTemplate;
    }

    @Bean
    @Qualifier("integrationRequestFactory")
    public HttpComponentsClientHttpRequestFactory integrationRequestFactory(@Qualifier("pooledIntegrationHttpClient") HttpClient httpClient) {
        return new HttpComponentsClientHttpRequestFactory(httpClient);
    }

    @Bean
    @Qualifier("apayRestTemplate")
    public RestTemplate apayRestTemplate(@Qualifier("integrationRequestFactory") HttpComponentsClientHttpRequestFactory requestFactory,
                                         ApplicationProperties applicationProperties,
                                         ObjectMapper objectMapper,
                                         LocalizationService localizationService) {
        return new RestTemplateBuilder()
                .requestFactory(() -> requestFactory)
                .uriTemplateHandler(new DefaultUriBuilderFactory(applicationProperties.getApay().getHost()))
                .errorHandler(new ApayErrorHandler(objectMapper, localizationService))
                .build();
    }

    @Bean
    @Qualifier("turonRestTemplate")
    public RestTemplate turonRestTemplate(ApplicationProperties applicationProperties, ObjectMapper objectMapper) {
        return new RestTemplateBuilder()
                .uriTemplateHandler(new DefaultUriBuilderFactory(applicationProperties.getTrueApiUrl()))
                .errorHandler(new ResponseErrorHandler() {
                    @Override
                    public boolean hasError(@NotNull ClientHttpResponse response) throws IOException {
                        return !response.getStatusCode().is2xxSuccessful();
                    }

                    @Override
                    public void handleError(@NotNull ClientHttpResponse response) throws IOException {
                        String turonError = new String(response.getBody().readAllBytes());
                        String errorMessage = null;
                        try {
                            Map<String, Object> map = objectMapper.reader().readValue(turonError, Map.class);
                            errorMessage = map.getOrDefault("error_message", null).toString();
                        } catch (Exception ignored) {
                        }
                        if (StringUtils.isBlank(errorMessage)) {
                            if (response.getStatusCode().value() == 400) {
                                errorMessage = "Некорректные параметры вызова метода";
                            } else if (response.getStatusCode().value() == 401) {
                                errorMessage = "Недействительный токен";
                            } else if (response.getStatusCode().value() == 403) {
                                errorMessage = "Доступ к ресурсу запрещен";
                            } else if (response.getStatusCode().value() == 500) {
                                errorMessage = "Произошла ошибка на сервере Turon";
                            } else if (response.getStatusCode().value() == 408) {
                                errorMessage = "Сервер проверки маркировки временно не работает. Попробуйте чуть позже";
                            } else {
                                errorMessage = turonError;
                            }
                        }
                        log.error(turonError);
                        throw new BadRequestException(errorMessage);
                    }
                })
                .build();
    }
}
