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
public class MarkStatsDTO implements Serializable {
    private Long productQuantity;
    private Integer quantity;
    private Integer received;
    private Integer remaining;

    public MarkStatsDTO(BigInteger productQuantity, Integer quantity, Integer received, Integer remaining) {
        this.productQuantity = productQuantity != null ? productQuantity.longValue() : 0L;
        this.quantity = quantity;
        this.received = received;
        this.remaining = remaining;
    }
}
