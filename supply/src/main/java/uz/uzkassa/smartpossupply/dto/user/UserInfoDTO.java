package uz.uzkassa.smartpossupply.dto.user;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import uz.uzkassa.smartpossupply.dto.base.CommonDTO;
import uz.uzkassa.smartpossupply.enums.Role;
import uz.uzkassa.smartpossupply.enums.UserStatus;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UserInfoDTO extends CommonDTO {
    String phone;
    Role role;
    UserStatus status;
}
