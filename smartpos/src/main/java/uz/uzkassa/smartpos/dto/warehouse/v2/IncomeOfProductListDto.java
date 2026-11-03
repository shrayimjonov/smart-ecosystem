package uz.uzkassa.smartpos.dto.warehouse.v2;

import lombok.Getter;
import lombok.Setter;
import uz.uzkassa.smartpos.dto.base.CommonDTO;
import uz.uzkassa.smartpos.dto.factura.EDocDto;
import uz.uzkassa.smartpos.enums.IncomeOfProductStatus;
import uz.uzkassa.smartpos.enums.IncomeStatus;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * DTO for {@link uz.uzkassa.smartpos.domain.postgres.warehouse.IncomeOfProduct}
 */
@Getter
@Setter
public class IncomeOfProductListDto implements Serializable {
    private Long id;
    private LocalDateTime createdDate = LocalDateTime.now();
    private String documentNo;
    private LocalDate documentDate;
    private LocalDateTime incomeDate;
    private String description;
    private BigDecimal totalSumma;
    private CommonDTO toBranch;
    private EDocDto invoice;
    private IncomeStatus status = IncomeStatus.SUCCESS;
    private IncomeOfProductStatus incomeStatus = IncomeOfProductStatus.DRAFT;
}