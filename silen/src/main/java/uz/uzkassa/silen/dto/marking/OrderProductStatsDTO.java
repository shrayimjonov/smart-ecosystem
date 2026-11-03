package uz.uzkassa.silen.dto.marking;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.io.Serializable;
import java.util.LinkedList;
import java.util.List;

/**
 * A DTO for the {@link uz.uzkassa.silen.domain.OrderProduct} entity.
 */
@Getter
@Setter
@ToString
public class OrderProductStatsDTO implements Serializable {
    private String productId;
    private String productName;
    private Integer quantity;
    private Long usedQuantity;
    private Long unusedQuantity;
    private List<String> parties;

    public List<String> getParties() {
        if (this.parties == null) {
            this.parties = new LinkedList<>();
        }
        return parties;
    }
}
