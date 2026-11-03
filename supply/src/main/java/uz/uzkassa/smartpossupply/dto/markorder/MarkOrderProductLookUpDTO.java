package uz.uzkassa.smartpossupply.dto.markorder;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import uz.uzkassa.smartpossupply.dto.base.CommonDTO;
import uz.uzkassa.smartpossupply.enums.ProductType;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class MarkOrderProductLookUpDTO extends CommonDTO {

    String barcode;

    String productType;

    BigDecimal qtyInPallet;

    BigDecimal qtyInBox;

    Integer expiredInMonth;

    public MarkOrderProductLookUpDTO(Long id, String name, String barcode) {
        super(id, name);
        this.barcode = barcode;
    }

    public MarkOrderProductLookUpDTO(Long id, String name, String barcode, ProductType productType) {
        super(id, name);
        this.barcode = barcode;
        this.productType = (productType != null) ? productType.getCode() : null;
    }

}
