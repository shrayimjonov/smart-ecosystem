package uz.uzkassa.silen.domain.redis;

import jakarta.persistence.Id;
import lombok.*;
import org.springframework.data.redis.core.RedisHash;

import java.io.Serializable;
import java.time.LocalDateTime;

@ToString
@RedisHash(value = "User", timeToLive = 82800)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class RedisUser implements Serializable {
    @Id
    private String id;
    private LocalDateTime logInDate;
}
