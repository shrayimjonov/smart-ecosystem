package uz.uzkassa.smartpossupply.dto.category;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import uz.uzkassa.smartpossupply.dto.base.CommonDTO;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class MobileCategoryTreeDTO  extends CommonDTO {

    Long productCount;

    Long parentId;

    Long branchId;

    @Schema
    List<MobileCategoryTreeDTO> children = new ArrayList<>();

    @SuppressWarnings("unused")
    public MobileCategoryTreeDTO(Long id, String name, BigInteger productCount, Long parentId, Long branchId) {
        this.id = id;
        this.name = name;
        this.productCount = productCount.longValue();
        this.parentId = parentId;
        this.branchId = branchId;
    }
}