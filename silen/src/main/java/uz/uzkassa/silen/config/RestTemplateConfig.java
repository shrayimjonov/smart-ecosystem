package uz.uzkassa.silen.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

@Configuration
public class RestTemplateConfig {

    @Bean("socketRestTemplate")
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }

}
