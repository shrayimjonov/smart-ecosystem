package uz.uzkassa.smartpos.dto.fashion;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import uz.uzkassa.smartpos.domain.postgres.fashion.FashionRegister;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class ColorGroupItemsRegisterDto implements Serializable {
    private Long id;
    private String colorCode;
    private String colorName;
    private String printName;
    private List<FashionItemRegisterDto> items = new ArrayList<>();

    public ColorGroupItemsRegisterDto(FashionRegister register) {
        if (register.getProductItem() != null && register.getProductItem().getColorGroup() != null) {
            this.id = register.getProductItem().getColorGroup().getGenericId();
            this.colorCode = register.getProductItem().getColorGroup().getColorCode();
            this.colorName = register.getProductItem().getColorGroup().getColorName();
            this.printName = register.getProductItem().getColorGroup().getPrintName();
        }
    }
}
