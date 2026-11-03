package uz.uzkassa.silen.dto.warehouse;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.io.Serializable;
import java.math.BigDecimal;

@Getter
@Setter
@ToString
public class StockDTO implements Serializable {
    private String organizationId;
    private String organizationName;
    private String productId;
    private String productName;
    private BigDecimal amount;
    private Integer bottleAmount;
}
