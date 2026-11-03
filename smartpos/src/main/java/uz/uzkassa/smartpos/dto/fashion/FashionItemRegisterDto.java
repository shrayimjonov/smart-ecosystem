package uz.uzkassa.smartpos.dto.fashion;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import uz.uzkassa.smartpos.domain.postgres.fashion.FashionRegister;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
public class FashionItemRegisterDto {
    private String sizeValue;
    private BigDecimal qty;

    public FashionItemRegisterDto(FashionRegister register) {
        if (register.getProductItem() != null) {
            this.sizeValue = register.getProductItem().getSizeValue();
        }
        this.qty = register.getQty();
    }
}
