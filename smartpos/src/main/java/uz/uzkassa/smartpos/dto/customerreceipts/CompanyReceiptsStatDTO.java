package uz.uzkassa.smartpos.dto.customerreceipts;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.math.BigDecimal;

@Getter
@Setter
@ToString
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class CompanyReceiptsStatDTO {
    private Long companyCount;

    private int branchCount;

    private Long receiptCount;

    private BigDecimal revenueCount;

    private int terminalCount;

    public CompanyReceiptsStatDTO(int branchCount, int terminalCount) {
        this.branchCount = branchCount;
        this.terminalCount = terminalCount;
    }
}
