package uz.uzkassa.smartpossupply.domain.xtrace;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;
import org.springframework.data.redis.core.TimeToLive;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.concurrent.TimeUnit;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
@RedisHash(value = "XtraceToken")
public class RedisXtraceToken implements Serializable {

    @Id
    Long companyId;

    String apiKey;

    String apiKeyId;

    LocalDateTime expireDate;

    @TimeToLive(unit = TimeUnit.SECONDS)
    Long ttl;
}