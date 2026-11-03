package uz.uzkassa.horeca.dto.product;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import uz.uzkassa.horeca.dto.base.CommonDTO;
import uz.uzkassa.horeca.enums.ProductOrigin;
import uz.uzkassa.horeca.enums.Vat;

import java.io.Serial;
import java.io.Serializable;
import java.util.Objects;

/**
 * Powered by: Shuxratjon Rayimjonov
 * Date: 01.05.2024
 */
@Getter
@Setter
@FieldDefaults(level = AccessLevel.PROTECTED)
@Builder
public class ProductCommonDTO implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    Long id;

    String name;

    String barcode;

    String catalogCode;

    Vat vat;

    ProductOrigin origin;

    boolean hasMark;

    String packageCode;

    String packageName;

    CommonDTO category;

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof ProductCommonDTO)) {
            return false;
        }
        return Objects.equals(id, ((ProductCommonDTO) o).id);
    }
}
