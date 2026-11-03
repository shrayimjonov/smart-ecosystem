package uz.uzkassa.smartpos.dto.analytics;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
public class UserSalesStatsDTO {
    private String name;

    private Boolean dismissed = false;

    private String branchName;

    private BigDecimal salesTotal;

    private Long salesCount;

    private BigDecimal averageReceiptCost;

}
