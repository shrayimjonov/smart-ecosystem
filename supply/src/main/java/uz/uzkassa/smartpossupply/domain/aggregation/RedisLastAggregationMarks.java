package uz.uzkassa.smartpossupply.domain.aggregation;


import jakarta.persistence.Id;
import lombok.Builder;
import lombok.Data;
import lombok.ToString;
import org.springframework.data.redis.core.RedisHash;
import org.springframework.data.redis.core.index.Indexed;

import java.io.Serializable;
import java.time.LocalDateTime;

@ToString
@RedisHash(value = "LastAggregationMarks", timeToLive = 3600)
@Data
@Builder
public class RedisLastAggregationMarks implements Serializable {
    @Id
    private String id;

    @Indexed
    private Long userId;

    @Builder.Default
    private LocalDateTime scanTime = LocalDateTime.now();

    private String userLogin;

    private Integer unit;

    public RedisLastAggregationMarks(String id,  Long userId, LocalDateTime scanTime, String userLogin, Integer unit) {
        this.id = id;
        this.userId = userId;
        this.scanTime = scanTime;
        this.userLogin = userLogin;
        this.unit = unit;
    }
}

