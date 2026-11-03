package uz.uzkassa.smartpossupply.config;

import lombok.extern.slf4j.Slf4j;
import org.apache.hc.client5.http.classic.HttpClient;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.http.converter.json.GsonHttpMessageConverter;
import org.springframework.web.client.RestTemplate;
import uz.uzkassa.smartpossupply.interceptor.RestTemplateCorrelationIdInterceptor;
import uz.uzkassa.smartpossupply.interceptor.RestTemplateLoggingInterceptor;
import uz.uzkassa.smartpossupply.interceptor.RestTemplateResponseErrorHandler;

import java.util.ArrayList;
import java.util.List;

@Configuration(proxyBeanMethods = false)
@Slf4j
public class RestTemplateConfig {

    @Bean
    @Primary
    public RestTemplate restTemplate() {
        SimpleClientHttpRequestFactory factory = new SimpleClientHttpRequestFactory();
        factory.setConnectTimeout(10000);
        factory.setReadTimeout(30000);
        RestTemplate restTemplate = new RestTemplate(factory);
        restTemplate.getInterceptors().add(new RestTemplateCorrelationIdInterceptor());
        restTemplate.getInterceptors().add(new RestTemplateLoggingInterceptor());
        restTemplate.setErrorHandler(new RestTemplateResponseErrorHandler());
        return restTemplate;
    }

    @Bean
    @Qualifier("facturaRestTemplate")
    public RestTemplate facturaRestTemplate(@Qualifier("integrationRequestFactory") HttpComponentsClientHttpRequestFactory requestFactory) {
        return this.buildIntegrationTemplate(requestFactory);
    }

    @Bean
    @Qualifier("tasnifRestTemplate")
    public RestTemplate tasnifRestTemplate(@Qualifier("integrationRequestFactory") HttpComponentsClientHttpRequestFactory requestFactory) {
        return this.buildIntegrationTemplate(requestFactory);
    }

    @Bean
    @Qualifier("aslBelgiRestTemplate")
    public RestTemplate aslBelgiRestTemplate(@Qualifier("integrationRequestFactory") HttpComponentsClientHttpRequestFactory requestFactory) {
        return this.buildIntegrationTemplate(requestFactory);
    }

    private RestTemplate buildIntegrationTemplate(
            HttpComponentsClientHttpRequestFactory requestFactory) {
        return new RestTemplateBuilder()
                .requestFactory(() -> requestFactory)
                .additionalInterceptors(new RestTemplateCorrelationIdInterceptor())
                .build();
    }

    @Bean("webSocketRestTemplate")
    public RestTemplate webSocketRestTemplate() {
        SimpleClientHttpRequestFactory factory = new SimpleClientHttpRequestFactory();
        factory.setConnectTimeout(10000);
        factory.setReadTimeout(30000);
        RestTemplate restTemplate = new RestTemplate(factory);
        restTemplate.getInterceptors().add(new RestTemplateCorrelationIdInterceptor());
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
}
