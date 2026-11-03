package uz.uzkassa.silen.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;
import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
public class TopSelectItem implements Serializable {
    protected String productId;
    protected String productName;
    protected BigDecimal quantity;


    public TopSelectItem(String id, String name, BigDecimal quantity) {
        this.productId = id;
        this.productName = name;
        this.quantity = quantity;
    }
}
