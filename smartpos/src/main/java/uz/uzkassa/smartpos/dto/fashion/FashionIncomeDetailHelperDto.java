package uz.uzkassa.smartpos.dto.fashion;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import uz.uzkassa.smartpos.domain.postgres.fashion.FashionIncomeOfProductDetail;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

/**
 * DTO for {@link FashionIncomeOfProductDetail}
 */
@Getter
@Setter
@NoArgsConstructor
public class FashionIncomeDetailHelperDto extends FashionIncomeDetailDto implements Serializable {
    Map<Long, FashionIncomeColorGroupDto> colorGroupMap = new HashMap<>();

    public FashionIncomeDetailHelperDto(FashionIncomeOfProductDetail detail) {
        super(detail);
    }
}