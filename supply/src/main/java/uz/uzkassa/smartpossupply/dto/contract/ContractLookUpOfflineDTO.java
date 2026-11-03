package uz.uzkassa.smartpossupply.dto.contract;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.io.Serializable;
import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ContractLookUpOfflineDTO implements Serializable {

    Long id;

    LocalDate contractDate;

    String contractNumber;

    Long customerId;
}
