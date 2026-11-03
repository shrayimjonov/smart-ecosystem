package uz.uzkassa.smartpossupply.dto.reconciliationact;

import lombok.Getter;
import lombok.Setter;
import org.springframework.data.domain.PageImpl;
import uz.uzkassa.smartpossupply.dto.base.CommonDTO;

import java.math.BigDecimal;

@Getter
@Setter
public class ReconciliationActDTO {
    CommonDTO company;
    CommonDTO customer;
    CommonDTO companyOwner;
    CommonDTO customerOwner;
    PageImpl<FinancialOperationACTDTO> financialOperations;
    BigDecimal totalDebitAmount;
    BigDecimal totalCreditAmount;
}
