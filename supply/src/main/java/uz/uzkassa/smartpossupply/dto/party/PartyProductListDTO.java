package uz.uzkassa.smartpossupply.dto.party;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import uz.uzkassa.smartpossupply.dto.base.CommonDTO;
import uz.uzkassa.smartpossupply.enums.PackageType;
import uz.uzkassa.smartpossupply.enums.ProductType;

import java.io.Serializable;

@Getter
@Setter
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class PartyProductListDTO implements Serializable {

    Long id;

    Long aggregations;

    Long marks;

    PartyProductItemDTO product;

    CommonDTO aic;

    public PartyProductListDTO(Long id, Long productId, String productName, String productBarcode, String productType, Long aggregations, Long marks, String packageType, String aic, Long aicId) {
        this.id = id;
        this.aggregations = aggregations;
        this.marks = marks;
        this.aic = new CommonDTO(aicId, aic);
        this.product = new PartyProductItemDTO(productId, productName, productBarcode, ProductType.valueOf(productType), PackageType.valueOf(packageType));
    }
}
