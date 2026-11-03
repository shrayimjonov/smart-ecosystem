package uz.uzkassa.smartpos.domain.redis;

import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;
import org.springframework.data.redis.core.TimeToLive;
import uz.uzkassa.smartpos.constants.TokenType;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@RedisHash(value = "TokenStore", timeToLive = 86400L)
public class TokenStoreHash {
    @Id
    @Enumerated(EnumType.STRING)
    private TokenType type;
    private String value;
    @TimeToLive
    private Long expirationInSeconds;
}
