package uz.uzkassa.horeca.dto.compensation;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import uz.uzkassa.horeca.dto.base.CommonDTO;
import uz.uzkassa.horeca.dto.category.CategoryListDTO;
import uz.uzkassa.horeca.dto.file.FileDTO;
import uz.uzkassa.horeca.dto.product.ProductPriceDTO;

import java.io.Serial;
import java.io.Serializable;
import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CompensationListCabinetDTO implements Serializable {

    @Serial
    private static final long serialVersionUID = 849584L;

    Long id;

    String description;

    ProductPriceDTO product;

    BigDecimal price;

    BigDecimal percent;

    CategoryListDTO category;

    FileDTO photo;
}
