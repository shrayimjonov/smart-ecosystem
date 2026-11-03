package uz.uzkassa.smartpos.dto.receipt;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.List;

@Getter
@Setter
public class ScheduleRequestDTO {
    private List<ProductScheduleRequestDTO> products;
    private Long branchId;
    private String originUid;
    private boolean forCredit;
    private BigDecimal discountPercent = BigDecimal.ZERO;
    private BigDecimal totalDiscount = BigDecimal.ZERO;
}
