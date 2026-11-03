package uz.uzkassa.cts.dto.category.admin;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import uz.uzkassa.cts.dto.base.CommonDTO;

import java.io.Serializable;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CategoryTreeAdminDTO extends CommonDTO implements Serializable {

    Long parentId;

    Long productCount;

    List<CategoryTreeAdminDTO> children = new ArrayList<>();

    @SuppressWarnings("unused")
    public CategoryTreeAdminDTO(Long id, String name, Long parentId, BigInteger productCount) {
        this.id = id;
        this.name = name;
        this.parentId = parentId;
        this.productCount = productCount.longValue();
    }
}
