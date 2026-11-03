package uz.uzkassa.smartpos.dto.scheduling.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public class ProductScheduleResponse implements Cloneable {
    private Long id;
    private BigDecimal totalCost = BigDecimal.ZERO;
    private BigDecimal totalVat;
    private BigDecimal totalPaid = BigDecimal.ZERO;
    private BigDecimal totalPaidVat;
    private BigDecimal vatPercent;
    private BigDecimal price = BigDecimal.ZERO;
    private BigDecimal qty = BigDecimal.ZERO;
    @Schema(hidden = true)
    private BigDecimal advancePercent;

    @Override
    public ProductScheduleResponse clone() {
        try {
            return (ProductScheduleResponse) super.clone();
        } catch (CloneNotSupportedException e) {
            throw new AssertionError();
        }
    }
}
