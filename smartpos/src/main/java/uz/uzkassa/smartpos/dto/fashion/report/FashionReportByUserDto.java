package uz.uzkassa.smartpos.dto.fashion.report;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public class FashionReportByUserDto {
    private Long userId;
    private String userFullName;
    private BigDecimal totalSumma;
    private BigDecimal totalDiscount;
    private BigDecimal avgSumma;
    private Long receiptsCount;
    private Long branchId;
    private String branchName;
}
