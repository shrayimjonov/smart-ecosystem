package uz.uzkassa.horeca.dto.menu;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import uz.uzkassa.horeca.dto.base.CommonDTO;
import uz.uzkassa.horeca.dto.unit.MultiUnitDTO;
import uz.uzkassa.horeca.enums.CategoryType;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class MenuItemDTO extends CommonDTO implements Serializable {

    @Serial
    private static final long serialVersionUID = 83459843L;

    Long menuId;

    Long productId;

    CategoryType categoryType;

    List<MultiUnitDTO> units;
}
