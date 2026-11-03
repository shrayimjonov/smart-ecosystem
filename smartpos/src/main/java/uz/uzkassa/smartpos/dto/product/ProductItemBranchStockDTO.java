package uz.uzkassa.smartpos.dto.product;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProductItemBranchStockDTO implements Serializable {
    private Long branchId;
    private String branchName;
    private BigDecimal count;
}
