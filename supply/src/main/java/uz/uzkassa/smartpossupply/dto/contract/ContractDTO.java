package uz.uzkassa.smartpossupply.dto.contract;

import lombok.*;
import lombok.experimental.FieldDefaults;
import uz.uzkassa.smartpossupply.enums.CurrencyType;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ContractDTO implements Serializable {

    Long id;

    Long customerId;

    LocalDate contractDate;

    LocalDate expiryDate;

    BigDecimal price;

    CurrencyType currency;

    String contractNumber;

    String description;

    Long companyId;

    String mobileId;
}
