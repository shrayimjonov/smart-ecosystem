package uz.uzkassa.smartpos.dto.scheduling.request;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public final class ProductScheduleRequest {
    private Long id;
    private BigDecimal qty;
    private BigDecimal price;
    private BigDecimal advancePercent;
    private BigDecimal vatPercent;
}
