package uz.uzkassa.smartpossupply.dto.order.terminal;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

import java.math.BigDecimal;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class TerminalTopProductsStatisticsDTO {
    Long productId;
    String productName;
    BigDecimal totalBrutto;
    BigDecimal totalNetto;
    BigDecimal qty;
    BigDecimal qtyInBox;
    BigDecimal totalSellingPrice;

    public TerminalTopProductsStatisticsDTO(Long productId,
                                            BigDecimal totalBrutto,
                                            BigDecimal totalNetto,
                                            BigDecimal qty,
                                            BigDecimal qtyInBox,
                                            BigDecimal totalSellingPrice,
                                            String productName
                                            ) {

        this.productId = productId;
        this.productName = productName;
        this.qty = qty;
        this.qtyInBox = qtyInBox;
        this.totalBrutto = totalBrutto;
        this.totalNetto = totalNetto;
        this.totalSellingPrice = totalSellingPrice;
    }
}
