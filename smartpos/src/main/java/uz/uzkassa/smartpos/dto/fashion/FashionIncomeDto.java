package uz.uzkassa.smartpos.dto.fashion;

import lombok.Getter;
import lombok.Setter;
import uz.uzkassa.smartpos.dto.UserShortDto;
import uz.uzkassa.smartpos.dto.base.CommonDTO;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

/**
 * DTO for {@link uz.uzkassa.smartpos.domain.postgres.fashion.FashionIncomeOfProduct}
 */
@Getter
@Setter
public class FashionIncomeDto extends FashionIncomeBaseDto implements Serializable {
    private Long id;
    private UserShortDto createdBy;
    private LocalDateTime createdDate;
    private UserShortDto lastModifiedBy;
    private LocalDateTime lastModifiedDate;
    private CommonDTO branch;
    private LocalDateTime incomeDate;
    private String documentNo;
    private String documentDate;
    private String description;
    private Set<FashionIncomeDetailDto> details = new HashSet<>();

}