package uz.uzkassa.silen.domain.redis;

import jakarta.persistence.Id;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.FieldDefaults;
import org.springframework.data.annotation.PersistenceCreator;
import org.springframework.data.redis.core.RedisHash;
import org.springframework.data.redis.core.index.Indexed;
import uz.uzkassa.silen.enumeration.AggregationMode;
import uz.uzkassa.silen.enumeration.PackageType;
import uz.uzkassa.silen.utils.Utils;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Set;

@ToString
@RedisHash("aggregation")
@FieldDefaults(level = AccessLevel.PRIVATE)
@Getter
@Setter
public class RedisAggregation implements Serializable {
    @Id
    String id;
    @Indexed
    String code;
    Set<String> marks;

    String productId;
    String serialNumber;
    String organizationId;
    boolean sendUtilise;
    boolean offline;
    LocalDateTime createdAt = LocalDateTime.now();
    LocalDateTime expirationDate;
    LocalDateTime productionDate;
    int unit;
    @Indexed
    AggregationMode aggregationMode;

    public RedisAggregation() {
    }

    @PersistenceCreator
    public RedisAggregation(String id, String code, Set<String> marks, String productId, String serialNumber, String organizationId, Boolean sendUtilise, Boolean offline, LocalDateTime createdAt, LocalDateTime expirationDate, LocalDateTime productionDate, Integer unit, AggregationMode aggregationMode) {
        this.id = id;
        this.code = code;
        this.marks = marks;
        this.productId = productId;
        this.serialNumber = serialNumber;
        this.organizationId = organizationId;
        this.offline = offline == null || offline;
        this.sendUtilise = sendUtilise == null || sendUtilise;
        this.createdAt = createdAt;
        this.expirationDate = expirationDate;
        this.productionDate = productionDate;
        this.aggregationMode = aggregationMode;
        if (unit == null) {
            if (Utils.validateCode(code, PackageType.BOX)) {
                this.unit = PackageType.BOX.getCode();
            } else if (Utils.validateCode(code, PackageType.PALLET)) {
                this.unit = PackageType.PALLET.getCode();
            } else if (Utils.validateCode(code, PackageType.BLOCK)) {
                this.unit = PackageType.BLOCK.getCode();
            }
        } else {
            this.unit = unit;
        }
    }

    public PackageType getPackageType() {
        return PackageType.fromCode(this.unit);
    }
}
