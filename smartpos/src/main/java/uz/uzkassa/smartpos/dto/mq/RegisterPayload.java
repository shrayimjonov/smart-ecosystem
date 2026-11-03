package uz.uzkassa.smartpos.dto.mq;

import lombok.*;
import uz.uzkassa.smartpos.enums.RegisterOperation;

import java.io.Serializable;
import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RegisterPayload implements Serializable {
    private Long branchId;
    private Long fromBranchId;
    private Long productId;
    private Long unitId;
    private BigDecimal amount;
    private BigDecimal costPrice;
    private BigDecimal salesPrice;
    private Integer markingCount;
    private RegisterOperation operation;

    public BigDecimal getCostPrice() {
        return costPrice != null ? costPrice : BigDecimal.ZERO;
    }

    public BigDecimal getSalesPrice() {
        return salesPrice != null ? salesPrice : BigDecimal.ZERO;
    }

    public Integer getMarkingCount() {
        return markingCount != null ? markingCount : 0;
    }
}
