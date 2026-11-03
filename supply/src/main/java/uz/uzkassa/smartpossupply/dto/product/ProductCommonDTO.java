package uz.uzkassa.smartpossupply.dto.product;

import lombok.*;
import lombok.experimental.FieldDefaults;
import uz.uzkassa.smartpossupply.dto.base.CommonDTO;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ProductCommonDTO extends CommonDTO {

    String barcode;

    String vatBarcode;

    String shortName;

    public ProductCommonDTO(Long id, String name, String barcode, String vatBarcode) {
        super(id, name);
        this.barcode = barcode;
        this.vatBarcode = vatBarcode;
    }

    public ProductCommonDTO(Long id, String name, String shortName, String barcode, String vatBarcode) {
        super(id, name);
        this.barcode = barcode;
        this.vatBarcode = vatBarcode;
        this.shortName = shortName;
    }
}
