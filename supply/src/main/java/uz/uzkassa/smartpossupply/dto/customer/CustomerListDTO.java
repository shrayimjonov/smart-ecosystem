package uz.uzkassa.smartpossupply.dto.customer;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import uz.uzkassa.smartpossupply.dto.address.AddressDetailDTO;
import uz.uzkassa.smartpossupply.dto.base.CommonDTO;
import uz.uzkassa.smartpossupply.dto.base.UserCommonDTO;
import uz.uzkassa.smartpossupply.dto.company.CompanyBaseDTO;
import uz.uzkassa.smartpossupply.enums.CompanyStatus;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
public class CustomerListDTO extends CompanyBaseDTO {

    String brand;

    AddressDetailDTO address;

    String contactPerson;

    String phone;

    CompanyStatus status;

    CommonDTO activityType;

    int customerBranchCount;

    int equipmentCount;

    BigDecimal limitBalance;

    BigDecimal currentBalance;

    boolean isHasLimit;

    UserCommonDTO creator;

    LocalDateTime createdDate;
}
