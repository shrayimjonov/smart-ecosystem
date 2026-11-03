package uz.uzkassa.smartpossupply.dto.customer.mobile;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import uz.uzkassa.smartpossupply.dto.activitytype.ActivityTypeDTO;
import uz.uzkassa.smartpossupply.dto.address.AddressDetailDTO;
import uz.uzkassa.smartpossupply.dto.company.CompanyBaseDTO;
import uz.uzkassa.smartpossupply.dto.companybank.CompanyBankDetailDTO;
import uz.uzkassa.smartpossupply.dto.product.ProductLookUpDTO;
import uz.uzkassa.smartpossupply.enums.CompanyType;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;


@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
@NoArgsConstructor
public class CustomerDetailMobileDTO extends CompanyBaseDTO {

    AddressDetailDTO address;

    String contactPerson;

    String phone;

    CompanyBankDetailDTO bankDetail;

    String director;

    String accountant;

    boolean hasFinancialDiscount;

    List<ProductLookUpDTO> financialDiscountProducts;

    ActivityTypeDTO activityType;

    CompanyType companyType;

    BigDecimal limitBalance;

    BigDecimal currentBalance;

    boolean hasLimit;

    boolean isMine;

    LocalDateTime createdDate;
}
