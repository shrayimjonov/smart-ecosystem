package uz.uzkassa.smartpos.dto.terminal.company;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import uz.uzkassa.smartpos.domain.postgres.company.Company;
import uz.uzkassa.smartpos.dto.CityDTO;
import uz.uzkassa.smartpos.dto.RegionDTO;
import uz.uzkassa.smartpos.enums.CompanyType;
import uz.uzkassa.smartpos.enums.PaymentType;

import java.time.LocalDateTime;
import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
public class TerminalCompanyDto {
    private Long id;
    private LocalDateTime createdDate;
    private String tin;
    private String name;
    private String address;
    private RegionDTO region;
    private CityDTO city;
    private Set<PaymentType> paymentTypes;
    private CompanyType companyType;
    private String accountNumber;
    private String mfo;
    private String bankName;

    public TerminalCompanyDto(Company company) {
        this.id = company.getId();
        this.createdDate = LocalDateTime.now();
        this.tin = company.getInn();
        this.name = company.getName();
        this.address = company.getAddress();
        if (company.getRegion() != null) {
            this.region = company.getRegion().getDTO();
        }
        if (company.getCity() != null) {
            this.city = company.getCity().getDTO();
        }
        if (company.getPaymentTypes() != null && !company.getPaymentTypes().isEmpty()) {
            this.paymentTypes = company.getPaymentTypes();
        }
        this.companyType = company.getCompanyType();
        this.accountNumber = company.getAccountNumber();
        this.mfo = company.getMfo();
        if (company.getBank() != null) {
            this.bankName = company.getBank().getName();
        }
    }
}

