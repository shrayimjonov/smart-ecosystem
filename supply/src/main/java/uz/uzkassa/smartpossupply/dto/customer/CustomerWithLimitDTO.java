package uz.uzkassa.smartpossupply.dto.customer;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import uz.uzkassa.smartpossupply.dto.company.CompanyBaseDTO;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
public class CustomerWithLimitDTO extends CompanyBaseDTO {

    BigDecimal limitBalance;

    BigDecimal currentBalance;

    boolean hasLimit;

    String phone;

    public CustomerWithLimitDTO(CompanyBaseDTO companyBaseDTO, BigDecimal limitBalance, BigDecimal currentBalance, boolean hasLimit, String phone) {
        super(companyBaseDTO.getId(), companyBaseDTO.getName(), companyBaseDTO.getTin());
        this.limitBalance = limitBalance;
        this.currentBalance = currentBalance;
        this.hasLimit = hasLimit;
        this.phone = phone;
    }

    public CustomerWithLimitDTO(CompanyBaseDTO companyBaseDTO, BigDecimal limitBalance, BigDecimal currentBalance, boolean hasLimit, String phone, String brand) {
        super(companyBaseDTO.getId(), companyBaseDTO.getName(), companyBaseDTO.getTin());
        this.limitBalance = limitBalance;
        this.currentBalance = currentBalance;
        this.hasLimit = hasLimit;
        this.phone = phone;
        this.brand = brand;
    }
}
