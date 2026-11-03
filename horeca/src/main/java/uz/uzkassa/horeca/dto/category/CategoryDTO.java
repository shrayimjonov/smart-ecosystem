package uz.uzkassa.horeca.dto.category;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import uz.uzkassa.horeca.dto.base.CommonDTO;
import uz.uzkassa.horeca.enums.CategoryType;
import uz.uzkassa.horeca.enums.ProductOrigin;
import uz.uzkassa.horeca.enums.Vat;

import java.io.Serial;
import java.io.Serializable;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CategoryDTO extends CommonDTO implements Serializable {

    @Serial
    private static final long serialVersionUID = 123991L;

    Long branchId;

    Long parentId;

    CatalogDTO catalog;

    CategoryType type;

    String packageName;

    String packageCode;

    ProductOrigin origin;

    Vat vat;

    @JsonIgnore
    UUID uniqueId;
}
