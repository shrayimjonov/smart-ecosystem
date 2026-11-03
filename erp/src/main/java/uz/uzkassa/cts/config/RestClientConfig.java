package uz.uzkassa.cts.config;

import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.web.client.RestClient;

@Configuration
public class RestClientConfig {

    @Qualifier("defaultPooledHttpClient")
    private final CloseableHttpClient httpClient;

    public RestClientConfig(CloseableHttpClient httpClient) {
        this.httpClient = httpClient;
    }

    @Bean("restClient")
    public RestClient restClient() {
       return RestClient.builder()
                .requestFactory(new HttpComponentsClientHttpRequestFactory(httpClient))
                .build();
    }



}
