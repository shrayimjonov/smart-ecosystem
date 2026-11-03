package uz.uzkassa.smartpossupply.dto.contract;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import uz.uzkassa.smartpossupply.dto.base.CommonDTO;
import uz.uzkassa.smartpossupply.enums.ContractStatus;
import uz.uzkassa.smartpossupply.enums.CurrencyType;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ContractDetailDTO implements Serializable {

    Long id;

    CommonDTO customer;

    LocalDate contractDate;

    String contractNumber;

    LocalDate expiryDate;

    BigDecimal price;

    CurrencyType currency;

    String description;

    ContractStatus status;
}
