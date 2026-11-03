package uz.uzkassa.horeca.domain.redis;

import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.redis.core.RedisHash;
import org.springframework.data.redis.core.TimeToLive;

import java.util.concurrent.TimeUnit;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@RedisHash("AccountBlockList")
public class AccountBlockList {

    @Id
    private Long id;

    @TimeToLive(unit = TimeUnit.SECONDS)
    private Long ttl;

    public AccountBlockList(Long id) {
        this.id = id;
    }
}
