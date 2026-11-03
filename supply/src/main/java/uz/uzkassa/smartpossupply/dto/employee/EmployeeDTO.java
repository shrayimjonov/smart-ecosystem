package uz.uzkassa.smartpossupply.dto.employee;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import uz.uzkassa.smartpossupply.dto.user.FullNameDTO;
import uz.uzkassa.smartpossupply.enums.Role;

import java.util.List;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class EmployeeDTO extends FullNameDTO {

    Long id;

    String tin;

    String pinfl;

    String login;

    String password;

    Role role;

    Long supervisorId;

    Long warehouseId;

    Long companyId;

    List<Long> branchIds;
}
