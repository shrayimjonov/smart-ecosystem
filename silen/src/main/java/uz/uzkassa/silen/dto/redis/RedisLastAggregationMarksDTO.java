package uz.uzkassa.silen.dto.redis;

import lombok.*;
import uz.uzkassa.silen.enumeration.PackageType;

import java.io.Serializable;
import java.time.LocalDateTime;

@ToString
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RedisLastAggregationMarksDTO implements Serializable {
    private String code;
    private String printCode;
    private String productName;
    private String userName;
    private Long sessionId;
    private LocalDateTime scanTime;
    private PackageType unit;
    private boolean child;
}
