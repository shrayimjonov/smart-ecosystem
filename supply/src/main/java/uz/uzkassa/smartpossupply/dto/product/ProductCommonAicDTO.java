package uz.uzkassa.smartpossupply.dto.product;

import lombok.*;
import lombok.experimental.FieldDefaults;
import uz.uzkassa.smartpossupply.dto.base.CommonDTO;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ProductCommonAicDTO extends CommonDTO {

    String barcode;

    String tnved;

    public ProductCommonAicDTO(Long id, String name, String barcode, String tnved) {
        super(id, name);
        this.barcode = barcode;
        this.tnved = tnved;
    }
}
