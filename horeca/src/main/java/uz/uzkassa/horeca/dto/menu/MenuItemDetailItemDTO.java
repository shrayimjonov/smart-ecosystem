package uz.uzkassa.horeca.dto.menu;

import java.io.Serial;
import java.io.Serializable;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import uz.uzkassa.horeca.dto.base.CommonDTO;
import uz.uzkassa.horeca.dto.product.ProductCommonDTO;

@Getter
@Setter
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class MenuItemDetailItemDTO extends CommonDTO implements Serializable {

    @Serial
    private static final long serialVersionUID = 2234345L;

    ProductCommonDTO product;

    CommonDTO menu;

    CommonDTO department;
}
