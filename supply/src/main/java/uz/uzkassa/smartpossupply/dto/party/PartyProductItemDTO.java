package uz.uzkassa.smartpossupply.dto.party;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import uz.uzkassa.smartpossupply.dto.base.CommonDTO;
import uz.uzkassa.smartpossupply.enums.PackageType;
import uz.uzkassa.smartpossupply.enums.ProductType;

@Getter
@Setter
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class PartyProductItemDTO extends CommonDTO {

    String barcode;

    ProductType productType;

    PackageType packageType;

    public PartyProductItemDTO(Long id, String name, String barcode, ProductType productType, PackageType packageType) {
        this.id = id;
        this.name = name;
        this.barcode = barcode;
        this.productType = productType;
        this.packageType = packageType;
    }
}
