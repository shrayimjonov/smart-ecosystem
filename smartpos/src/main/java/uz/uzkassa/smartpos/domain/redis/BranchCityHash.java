package uz.uzkassa.smartpos.domain.redis;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@RedisHash("BranchCity")
public class BranchCityHash {
    @Id
    private Long branchId;
    private Long cityId;
}
