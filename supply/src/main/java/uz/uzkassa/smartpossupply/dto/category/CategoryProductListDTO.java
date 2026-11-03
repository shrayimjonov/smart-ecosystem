package uz.uzkassa.smartpossupply.dto.category;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import uz.uzkassa.smartpossupply.dto.base.CommonDTO;

@Getter
@Setter
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CategoryProductListDTO  extends CommonDTO {

    boolean isCategory;

    @SuppressWarnings("unused")
    public CategoryProductListDTO(Long id, String name, boolean isCategory) {
        this.id = id;
        this.name = name;
        this.isCategory = isCategory;
    }
}
