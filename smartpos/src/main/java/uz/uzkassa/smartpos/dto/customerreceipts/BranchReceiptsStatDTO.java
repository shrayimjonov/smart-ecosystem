package uz.uzkassa.smartpos.dto.customerreceipts;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.math.BigDecimal;

@Getter
@Setter
@ToString
public class BranchReceiptsStatDTO {
    private Long branchCount;
    private Long companyCount;
    private Long receiptCount;
    private BigDecimal revenueCount;
}
