package uz.uzkassa.smartpos.dto.fashion;

import lombok.Getter;
import lombok.Setter;
import uz.uzkassa.smartpos.dto.base.CommonDTO;

import java.io.Serializable;
import java.math.BigDecimal;

@Getter
@Setter
public class FashionBranchColorGroupRegisterDto extends FashionIncomeBaseDto implements Serializable {
    private String sizeValue;
    private CommonDTO branch;
    private CommonDTO unit;
    private BigDecimal qty = BigDecimal.ZERO;
    private BigDecimal costPrice;
    private BigDecimal salesPrice;
}
