package uz.uzkassa.smartpossupply.dto.employee.mobile;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import uz.uzkassa.smartpossupply.dto.base.CommonDTO;
import uz.uzkassa.smartpossupply.dto.branch.BranchCoordinateDTO;
import uz.uzkassa.smartpossupply.dto.user.FullNameDTO;
import uz.uzkassa.smartpossupply.enums.Role;
import uz.uzkassa.smartpossupply.enums.UserStatus;

import java.util.List;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class EmployeeListMobileDTO extends FullNameDTO {

    Long id;

    String login;

    Role role;

    UserStatus status;

    CommonDTO warehouse;

    List<BranchCoordinateDTO> branches;
}
