package uz.uzkassa.horeca.dto.menu;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import uz.uzkassa.horeca.dto.base.CommonDTO;
import uz.uzkassa.horeca.dto.file.FileDTO;
import uz.uzkassa.horeca.dto.product.ProductCommonDTO;
import uz.uzkassa.horeca.dto.unit.MultiUnitListDTO;
import uz.uzkassa.horeca.enums.CommonStatus;

@Getter
@Setter
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class MenuItemListDTO extends CommonDTO implements Serializable {

    @Serial
    private static final long serialVersionUID = 59934853L;

    ProductCommonDTO product;

    List<MultiUnitListDTO> units;

    FileDTO photo;

    boolean favourite;

    CommonDTO menu;

    CommonDTO parentMenu;

    List<FileDTO> photos;

    CommonStatus status;
}
