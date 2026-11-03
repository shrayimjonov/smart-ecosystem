package uz.uzkassa.cts.domain.redis;

import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;
import org.springframework.data.redis.core.TimeToLive;
import uz.uzkassa.cts.enums.IntegrationType;

import java.io.Serial;
import java.io.Serializable;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@RedisHash(value = "TokenStore", timeToLive = 2_592_000L)
@FieldDefaults(level = AccessLevel.PRIVATE)
public class TokenStore implements Serializable {

    @Serial
    private static final long serialVersionUID = 3891958629L;

    @Id
    @Enumerated(EnumType.STRING)
    IntegrationType type;

    String token;

    @TimeToLive
    Long expiration;
}
