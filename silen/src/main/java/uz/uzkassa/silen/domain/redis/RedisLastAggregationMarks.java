package uz.uzkassa.silen.domain.redis;

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
    private String productId;

    @Indexed
    private String userId;

    @Indexed
    private Long sessionId;

    private String printCode;

    @Builder.Default
    private LocalDateTime scanTime = LocalDateTime.now();

    private String userLogin;

    private Integer unit;

    private boolean child;

    @Builder.Default
    private Boolean reserved = false;

    public RedisLastAggregationMarks(String id, String productId, String userId, Long sessionId, String printCode, LocalDateTime scanTime, String userLogin, Integer unit, boolean child, Boolean reserved) {
        this.id = id;
        this.productId = productId;
        this.userId = userId;
        this.sessionId = sessionId;
        this.printCode = printCode;
        this.scanTime = scanTime;
        this.userLogin = userLogin;
        this.unit = unit;
        this.child = child;
        this.reserved = reserved;
    }
}
