package uz.uzkassa.silen.dto.warehouse;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@JsonInclude(JsonInclude.Include.NON_NULL)
public class StockStatsDTO {
    private String productId;
    private String name;
    private BigDecimal amount;
    private BigDecimal priceWithVat;
    private BigDecimal sumWithVat;
    private BigDecimal sumVat;
    private BigDecimal sumPriceWithoutVat;

    public StockStatsDTO(BigDecimal amount, BigDecimal sumWithVat, BigDecimal sumVat, BigDecimal sumPriceWithoutVat) {
        this.amount = amount;
        this.sumWithVat = sumWithVat;
        this.sumVat = sumVat;
        this.sumPriceWithoutVat = sumPriceWithoutVat;
    }
}
