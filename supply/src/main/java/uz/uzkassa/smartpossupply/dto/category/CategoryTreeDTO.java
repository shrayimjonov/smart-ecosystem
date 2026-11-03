package uz.uzkassa.smartpossupply.dto.category;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import uz.uzkassa.smartpossupply.dto.base.CommonDTO;
import uz.uzkassa.smartpossupply.enums.CommonStatus;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CategoryTreeDTO extends CommonDTO {

    Long parentId;

    Long productCount;

    CommonStatus status;

    @Schema
    List<CategoryTreeDTO> children = new ArrayList<>();

    @SuppressWarnings("unused") //It's used as a constructor query
    public CategoryTreeDTO(Long id, String name, Long parentId, BigInteger productCount, String status) {
        this.id = id;
        this.name = name;
        this.parentId = parentId;
        this.productCount = productCount != null ? productCount.longValue() : 0L;
        this.status = status != null ? CommonStatus.valueOf(status) : null;
    }
}
