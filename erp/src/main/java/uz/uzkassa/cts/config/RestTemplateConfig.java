package uz.uzkassa.cts.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.hc.client5.http.classic.HttpClient;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.http.converter.json.GsonHttpMessageConverter;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.DefaultUriBuilderFactory;
import uz.uzkassa.cts.interceptor.RestTemplateLoggingInterceptor;
import uz.uzkassa.cts.interceptor.RestTemplateResponseErrorHandler;
import uz.uzkassa.cts.validator.LocalizationMessage;

import java.util.ArrayList;
import java.util.List;

@Configuration
@Slf4j
public class RestTemplateConfig {

    @Bean
    public RestTemplate restTemplate() {
        SimpleClientHttpRequestFactory factory = new SimpleClientHttpRequestFactory();
        factory.setConnectTimeout(10000);
        factory.setReadTimeout(90000);
        RestTemplate restTemplate = new RestTemplate(factory);
        restTemplate.getInterceptors().add(new RestTemplateLoggingInterceptor());
        restTemplate.setErrorHandler(new RestTemplateResponseErrorHandler());
        return restTemplate;
    }

    @Bean("xFileRestTemplate")
    public RestTemplate xFileRestTemplate() {
        SimpleClientHttpRequestFactory factory = new SimpleClientHttpRequestFactory();
        factory.setConnectTimeout(30000);
        factory.setReadTimeout(30000);
        RestTemplate restTemplate = new RestTemplate(factory);
        restTemplate.getInterceptors().add(new RestTemplateLoggingInterceptor());
        restTemplate.setErrorHandler(new RestTemplateResponseErrorHandler());
        return restTemplate;
    }

    @Bean("kapitalLegalTemplate")
    public RestTemplate kapitalLegalTemplate() {
        SimpleClientHttpRequestFactory factory = new SimpleClientHttpRequestFactory();
        factory.setConnectTimeout(10000);
        factory.setReadTimeout(180000);
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
        restTemplate.getInterceptors().add(new RestTemplateLoggingInterceptor());
        restTemplate.setErrorHandler(new RestTemplateResponseErrorHandler());
        List<HttpMessageConverter<?>> converters = new ArrayList<>();
        converters.add(new StringHttpMessageConverter());
        converters.add(new GsonHttpMessageConverter());
        restTemplate.setMessageConverters(converters);
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
                                         ObjectMapper objectMapper,
                                         LocalizationMessage localizationMessage) {
        return new RestTemplateBuilder()
                .requestFactory(() -> requestFactory)
                .errorHandler(new ApayErrorHandler(objectMapper, localizationMessage))
                .build();
    }

    @Bean
    public DefaultUriBuilderFactory uriBuilderFactory() {
        return new DefaultUriBuilderFactory();
    }
}
