package uz.uzkassa.horeca.dto.category;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import uz.uzkassa.horeca.dto.base.CommonDTO;
import uz.uzkassa.horeca.dto.product.ProductDTO;
import uz.uzkassa.horeca.enums.CategoryType;
import uz.uzkassa.horeca.enums.CommonStatus;
import uz.uzkassa.horeca.enums.ProductOrigin;
import uz.uzkassa.horeca.enums.Vat;

import java.io.Serial;
import java.io.Serializable;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CategoryDetailDTO extends CommonDTO implements Serializable {

    @Serial
    private static final long serialVersionUID = 82455L;

    CatalogDTO catalog;

    CommonStatus status;

    CategoryType type;

    CommonDTO branch;

    CommonDTO parent;

    String packageName;

    String packageCode;

    Vat vat;

    ProductOrigin origin;
}
