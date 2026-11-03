package uz.uzkassa.smartpos.security;

import com.auth0.jwt.JWT;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;

public final class TokenUtils {
    public static LocalDateTime getExpirationTime(String token) {
        return JWT.decode(token).getExpiresAt()
                .toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime()
                .minusMinutes(10);
    }


    public static Long getExpirationInSeconds(String token) {
        return getExpirationTime(token).toEpochSecond(ZoneOffset.UTC) - LocalDateTime.now().toEpochSecond(ZoneOffset.UTC);
    }
}
