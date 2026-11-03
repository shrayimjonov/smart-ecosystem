package uz.uzkassa.smartpossupply.dto.acceptancetransferact;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import uz.uzkassa.smartpossupply.dto.base.CommonDTO;
import uz.uzkassa.smartpossupply.dto.company.CompanyBaseDTO;
import uz.uzkassa.smartpossupply.enums.AcceptanceTransferActStatus;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * Powered by: Shuxratjon Rayimjonov
 * Date: 07.06.2023
 */
@Getter
@Setter
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class AcceptanceTransferActListDTO implements Serializable {

    Long id;

    String actNo;

    LocalDate actDate;

    BigDecimal totalPrice;

    AcceptanceTransferActStatus status;

    CompanyBaseDTO buyer;

    CompanyBaseDTO seller;

    CommonDTO branch;
}
