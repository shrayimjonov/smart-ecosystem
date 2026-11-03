package uz.uzkassa.horeca.dto.category;

import lombok.*;
import lombok.experimental.FieldDefaults;
import uz.uzkassa.horeca.dto.base.CommonDTO;
import uz.uzkassa.horeca.enums.CategoryType;

import java.io.Serial;
import java.io.Serializable;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CategoryLookUpDTO extends CommonDTO implements Serializable {

    @Serial
    private static final long serialVersionUID = 3453453L;

    CategoryType type;

    CatalogDTO catalog;

    public CategoryLookUpDTO(Long id, String name, CategoryType type, CatalogDTO catalog) {
        super(id, name);
        this.type = type;
        this.catalog = catalog;
    }
}
