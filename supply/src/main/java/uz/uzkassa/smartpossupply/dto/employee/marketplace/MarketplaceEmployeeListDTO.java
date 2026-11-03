package uz.uzkassa.smartpossupply.dto.employee.marketplace;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import uz.uzkassa.smartpossupply.dto.user.FullNameDTO;
import uz.uzkassa.smartpossupply.enums.Role;
import uz.uzkassa.smartpossupply.enums.UserStatus;

@Setter
@Getter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class MarketplaceEmployeeListDTO {

    Long id;

    FullNameDTO name;

    String phone;

    Role role;

    UserStatus status;

}
