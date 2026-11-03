package uz.uzkassa.cts.dto.integration.billing;

import lombok.Getter;
import lombok.Setter;
import uz.uzkassa.cts.enums.CompanyType;

import java.io.Serializable;
import java.math.BigDecimal;

@Getter
@Setter
public class AccountStatementResponseDTO implements Serializable {
    private CompanyType companyType;
    private BigDecimal balance;
    private String tin;
    private String customerName;
    private Long customerId;
}
