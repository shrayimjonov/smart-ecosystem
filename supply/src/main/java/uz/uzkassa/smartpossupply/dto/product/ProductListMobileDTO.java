package uz.uzkassa.smartpossupply.dto.product;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import uz.uzkassa.smartpossupply.dto.base.CommonDTO;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ProductListMobileDTO extends CommonDTO {

    BigDecimal price;

    CommonDTO unit;

    List<ProductPhotoListDTO> photos = new ArrayList<>();

    public void addPhoto(ProductPhotoListDTO productPhotoListDTO) {
        photos.add(productPhotoListDTO);
    }
}
