package uz.uzkassa.smartpos.dto.integration.onec;

import lombok.Getter;
import lombok.Setter;
import uz.uzkassa.smartpos.dto.base.CommonDTO;

import java.time.LocalDate;
import java.util.List;

@Getter
@Setter
public class OneCIncomeResponse {
    private Long id;
    private String documentNo;
    private LocalDate documentDate;
    private CommonDTO toBranch;
    private List<OneCIncomeDetailDto> incomeOfProductDetails;
}
