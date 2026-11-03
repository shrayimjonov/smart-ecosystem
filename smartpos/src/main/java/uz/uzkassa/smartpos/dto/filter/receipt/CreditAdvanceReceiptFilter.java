package uz.uzkassa.smartpos.dto.filter.receipt;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CreditAdvanceReceiptFilter {
    private Long companyId;
    private Long branchId;
    @NotBlank
    private String status;
    private String search;
    private Integer pageNumber = 0;
    private Integer pageSize = 20;
    private String orderBy;
    private String orderDirection;
    private String originUid;
}
