package uz.uzkassa.smartpossupply.dto.acceptancetransferact;

import lombok.*;
import lombok.experimental.FieldDefaults;
import uz.uzkassa.smartpossupply.dto.base.CommonDTO;
import uz.uzkassa.smartpossupply.dto.company.CompanyBaseDTO;

import java.io.Serializable;
import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class AcceptanceTransferActLookUpDTO implements Serializable {

    Long id;

    String number;

    LocalDate date;

    CompanyBaseDTO customer;

    CommonDTO branch;
}
