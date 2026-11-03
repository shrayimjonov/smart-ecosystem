package uz.uzkassa.cts.dto.sale.contract;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.math.BigDecimal;

@Getter
@Setter
public class SaleContractAccessoryListCalDTO implements Serializable {
    private String accessoryName;
    private Integer qty;
    private BigDecimal price;
    private BigDecimal qtyPrice;
    private BigDecimal priceVat;
    private BigDecimal totalPriceWithVat;
}
