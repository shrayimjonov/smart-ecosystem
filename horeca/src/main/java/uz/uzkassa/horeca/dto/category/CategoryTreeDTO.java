package uz.uzkassa.horeca.dto.category;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import uz.uzkassa.horeca.dto.base.CommonDTO;
import uz.uzkassa.horeca.enums.CommonStatus;
import uz.uzkassa.horeca.enums.ProductOrigin;
import uz.uzkassa.horeca.enums.Vat;

import java.io.Serial;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CategoryTreeDTO extends CommonDTO implements Serializable {

    @Serial
    private static final long serialVersionUID = 7583475L;

    Long parentId;

    CommonStatus status;

    String packageCode;

    String packageName;

    Vat vat;

    ProductOrigin origin;

    List<CategoryTreeDTO> children = new ArrayList<>();

    @SuppressWarnings("unused")
    public CategoryTreeDTO(Long id, String name, Long parentId, CommonStatus status, String packageCode, String packageName, Vat vat, ProductOrigin origin) {
        this.id = id;
        this.name = name;
        this.parentId = parentId;
        this.status = status;
        this.packageCode = packageCode;
        this.packageName = packageName;
        this.vat = vat;
        this.origin = origin;
    }
}
