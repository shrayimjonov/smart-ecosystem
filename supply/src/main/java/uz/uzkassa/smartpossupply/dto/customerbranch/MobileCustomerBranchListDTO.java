package uz.uzkassa.smartpossupply.dto.customerbranch;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import uz.uzkassa.smartpossupply.dto.address.AddressDetailDTO;
import uz.uzkassa.smartpossupply.dto.base.CommonDTO;
import uz.uzkassa.smartpossupply.enums.CommonStatus;

import java.math.BigDecimal;

/**
 * Powered by Shuxratjon Rayimjonov
 * Date: 19.01.2023 15:41
 */
@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class MobileCustomerBranchListDTO extends CommonDTO {

    String customerBranchNumber;

    AddressDetailDTO address;

    CommonDTO branch;

    CommonDTO warehouse;

    CommonStatus status;

    String phone;

    BigDecimal currentBalance;

    boolean orderingBlocked;
}
