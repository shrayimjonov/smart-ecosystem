package uz.uzkassa.cts.dto.apay.category;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import uz.uzkassa.cts.dto.base.CommonDTO;

import java.io.Serializable;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
@NoArgsConstructor
public class APayCategoryDTO extends CommonDTO implements Serializable {

    String apayId;

    String parentId;

    public APayCategoryDTO(Long id, String name, String apayId, String parentId) {
        super(id, name);
        this.apayId = apayId;
        this.parentId = parentId;
    }
}
