package uz.uzkassa.smartpos.dto.integration.onec;

import lombok.Getter;
import lombok.Setter;
import uz.uzkassa.smartpos.dto.base.CommonDTO;

import java.time.LocalDate;

@Getter
@Setter
public class OneCIncomeListDto {
    private Long id;
    private String documentNo;
    private LocalDate documentDate;
    private CommonDTO toBranch;
}
