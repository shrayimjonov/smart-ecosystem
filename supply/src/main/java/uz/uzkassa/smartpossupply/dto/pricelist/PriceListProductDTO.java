package uz.uzkassa.smartpossupply.dto.pricelist;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import uz.uzkassa.smartpossupply.dto.base.CommonDTO;

@Getter
@Setter
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class PriceListProductDTO extends CommonDTO {
    String barcode;

    String vatBarcode;

    String capacity;

    public PriceListProductDTO(Long id, String name, String barcode, String vatBarcode, String capacity) {
        super(id, name);
        this.barcode = barcode;
        this.vatBarcode = vatBarcode;
        this.capacity = capacity;
    }
}
