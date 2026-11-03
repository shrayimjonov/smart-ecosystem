package uz.uzkassa.smartpossupply.dto.category;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import uz.uzkassa.smartpossupply.dto.base.CommonDTO;

import java.math.BigInteger;

/**
 * Powered by: Shuxratjon Rayimjonov
 * Date: 07.04.2023 17:42
 */
@Getter
@Setter
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class MobileCategoryListDTO extends CommonDTO {

    Long productCount;

    @SuppressWarnings("unused")
    public MobileCategoryListDTO(Long id, String name, BigInteger productCount) {
        this.id = id;
        this.name = name;
        this.productCount = productCount.longValue();
    }
}
