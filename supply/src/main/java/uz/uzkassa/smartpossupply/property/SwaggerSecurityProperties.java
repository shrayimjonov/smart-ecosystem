package uz.uzkassa.smartpossupply.property;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "springdoc.security")
public record SwaggerSecurityProperties(String username, String password, String url) {
}
