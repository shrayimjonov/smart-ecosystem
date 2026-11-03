package uz.uzkassa.smartpossupply.dto.company;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import uz.uzkassa.smartpossupply.dto.base.CommonDTO;
import uz.uzkassa.smartpossupply.dto.user.OwnerDTO;
import uz.uzkassa.smartpossupply.enums.CompanyStatus;

import java.time.LocalDateTime;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
@NoArgsConstructor
public class CompanyListDTO extends CompanyBaseDTO {

    LocalDateTime registrationDate;

    OwnerDTO owner;

    CommonDTO manager;

    CompanyStatus status;

    boolean xFileEnabled;

    LocalDateTime managerAssignedDate;

    CommonDTO region;

    CommonDTO district;
}
