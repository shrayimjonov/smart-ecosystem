package uz.uzkassa.silen.domain.redis;


import jakarta.persistence.Id;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.FieldDefaults;
import org.springframework.data.redis.core.RedisHash;

@ToString
@RedisHash("blocked_party")
@FieldDefaults(level = AccessLevel.PRIVATE)
@Getter
@Setter
public class RedisBlockedParty {
    @Id
    String id;

    public RedisBlockedParty(String id) {
        this.id = id;
    }
}
