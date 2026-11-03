package uz.uzkassa.smartpossupply.dto.user.supervisor;

import lombok.*;
import lombok.experimental.FieldDefaults;
import uz.uzkassa.smartpossupply.dto.base.CommonDTO;
import uz.uzkassa.smartpossupply.enums.BusinessType;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
@AllArgsConstructor
@NoArgsConstructor
public class SupervisorCustomerDTO extends CommonDTO {
    String officialName;
    String phone;
    String tin;
    BusinessType type;
}
