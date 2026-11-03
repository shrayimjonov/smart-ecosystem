package uz.uzkassa.smartpos.dto.filter.receipt;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CreditReceiptFilter {
    @NotNull
    private Long companyId;
    private String uid;
    private String customerName;
    private String customerPhone;
    private String fiscalUrl;
    private Integer page;
    private Integer size;
}
