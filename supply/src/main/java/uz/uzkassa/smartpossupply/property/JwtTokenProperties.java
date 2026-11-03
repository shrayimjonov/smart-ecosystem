package uz.uzkassa.smartpossupply.property;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "security.authentication.jwt")
public record JwtTokenProperties(String secretKey, Long tokenValidityInSeconds,
                                 Long tokenValidityInSecondsForRememberMe, Long refreshTokenValidityInHours) {
}
