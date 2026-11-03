package uz.uzkassa.smartpos.dto.fashion;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import uz.uzkassa.smartpos.domain.postgres.fashion.FashionIncomeOfProductDetail;
import uz.uzkassa.smartpos.enums.RegisterOperation;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * DTO for {@link uz.uzkassa.smartpos.domain.postgres.fashion.FashionRegister}
 */
@Getter
@Setter
@NoArgsConstructor
public class FashionRegisterCreateDto implements Serializable {
    @NotNull
    private Long companyId;
    @NotNull
    private Long branchId;
    @NotNull
    private Long cityId;
    @NotNull
    private Long productId;
    @NotNull
    private Long unitId;
    @NotNull
    private BigDecimal qty;
    @NotNull
    private BigDecimal costPrice;
    @NotNull
    private BigDecimal salesPrice;
    private Long productItemId;
    @NotNull
    private RegisterOperation registerOperation;

    public FashionRegisterCreateDto(FashionIncomeOfProductDetail detail, Long companyId, RegisterOperation registerOperation) {
        this.companyId = companyId;
        this.branchId = detail.getBranchId();
        this.cityId = detail.getCityId();
        this.productId = detail.getProductId();
        this.unitId = detail.getUnitId();
        this.qty = detail.getQty();
        this.costPrice = detail.getCostPrice();
        this.salesPrice = detail.getSalesPrice();
        this.productItemId = detail.getProductItemId();
        this.registerOperation = registerOperation;
    }
}