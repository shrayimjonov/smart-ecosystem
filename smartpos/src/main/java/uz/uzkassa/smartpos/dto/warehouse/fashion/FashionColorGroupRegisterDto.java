package uz.uzkassa.smartpos.dto.warehouse.fashion;

import lombok.Getter;
import lombok.Setter;
import uz.uzkassa.smartpos.domain.postgres.fashion.FashionRegister;
import uz.uzkassa.smartpos.dto.base.CommonDTO;
import uz.uzkassa.smartpos.dto.fashion.FashionIncomeBaseDto;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * DTO for {@link FashionRegister}
 */
@Getter
@Setter
public class FashionColorGroupRegisterDto extends FashionIncomeBaseDto implements Serializable {
    private Long id;
    private String colorCode;
    private String colorName;
    private String printName;
    private CommonDTO unit;
    private BigDecimal qty = BigDecimal.ZERO;
    private BigDecimal costPrice;
    private BigDecimal salesPrice;
}