package uz.uzkassa.silen.dto.marking;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.io.Serializable;
import java.math.BigInteger;

@Getter
@Setter
@ToString
@NoArgsConstructor
public class AggregationStatsDTO implements Serializable {
    private Long palletQuantity;
    private Long boxQuantity;
    private Long bottleQuantity;
    private Long blockQuantity;
    private Long totalQuantity;

    public AggregationStatsDTO(BigInteger quantity, BigInteger boxQuantity, BigInteger bottleQuantity) {
        this.palletQuantity = quantity != null ? quantity.longValue() : 0L;
        this.boxQuantity = boxQuantity != null ? boxQuantity.longValue() : 0L;
        this.bottleQuantity = bottleQuantity != null ? bottleQuantity.longValue() : 0L;
        this.totalQuantity = this.palletQuantity + this.boxQuantity;
    }

    public AggregationStatsDTO(BigInteger quantity, BigInteger boxQuantity, BigInteger blockQuantity, BigInteger bottleQuantity) {
        this.palletQuantity = quantity != null ? quantity.longValue() : 0L;
        this.boxQuantity = boxQuantity != null ? boxQuantity.longValue() : 0L;
        this.blockQuantity = blockQuantity != null ? blockQuantity.longValue() : 0L;
        this.bottleQuantity = bottleQuantity != null ? bottleQuantity.longValue() : 0L;
        this.totalQuantity = this.palletQuantity + this.boxQuantity;
    }
}
