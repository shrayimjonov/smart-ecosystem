package uz.uzkassa.smartpos.dto.warehouse;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import uz.uzkassa.smartpos.dto.BaseAuditDTO;
import uz.uzkassa.smartpos.dto.SelectItem;
import uz.uzkassa.smartpos.enums.IncomeStatus;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@ToString
public class IncomeOfProductDTO extends BaseAuditDTO {
    private Long id;
    private String documentNo;
    private LocalDate documentDate;
    private String description;
    private SelectItem contractor;
    private SelectItem toBranch;
    private Long toBranchId;
    private Long agreementId;
    @NotNull
    private List<IncomeOfProductDetailDTO> incomeOfProductDetails;
    private BigDecimal totalSumma;
    private String invoiceNumber;
    private Long invoiceId;
    private LocalDateTime createdDate;
    private IncomeStatus status;
}
