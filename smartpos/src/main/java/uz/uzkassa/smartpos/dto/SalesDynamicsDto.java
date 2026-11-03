package uz.uzkassa.smartpos.dto;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
public class SalesDynamicsDto {
    private LocalDateTime date;
    private BigDecimal totalSales;
}
