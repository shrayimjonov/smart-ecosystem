package uz.uzkassa.cts.dto.employee.cabinet;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import uz.uzkassa.cts.dto.base.CommonDTO;
import uz.uzkassa.cts.dto.base.FullNameDTO;
import uz.uzkassa.cts.enums.Role;
import uz.uzkassa.cts.enums.UserStatus;

import java.util.List;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class EmployeeListCabinetDTO extends FullNameDTO {

    String username;

    String phone;

    String email;

    CommonDTO branch;

    CommonDTO department;

    CommonDTO position;

    List<Role> roles;

    UserStatus status;
}
