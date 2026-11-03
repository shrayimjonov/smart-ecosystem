package uz.uzkassa.smartpossupply.dto.contract;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import uz.uzkassa.smartpossupply.enums.ContractStatus;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ContractListDTO implements Serializable {

    Long id;

    LocalDate contractDate;

    LocalDate expiryDate;

    BigDecimal price;

    BigDecimal salePrice;

    String contractNumber;

    String description;

    ContractStatus status;
}
