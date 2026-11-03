package uz.uzkassa.smartpossupply.dto.employee;

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
public class EmployeeDetailDTO extends FullNameDTO {

    Long id;

    String tin;

    String pinfl;

    String login;

    Role role;

    CommonDTO supervisor;

    UserStatus status;

    CommonDTO warehouse;

    List<BranchCoordinateDTO> branches;
}
