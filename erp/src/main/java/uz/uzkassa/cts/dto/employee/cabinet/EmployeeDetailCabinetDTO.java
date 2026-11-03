package uz.uzkassa.cts.dto.employee.cabinet;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import uz.uzkassa.cts.dto.address.base.BaseAddressDetailDTO;
import uz.uzkassa.cts.dto.base.CommonDTO;
import uz.uzkassa.cts.dto.base.FullNameDTO;
import uz.uzkassa.cts.dto.contact.ContactListDTO;
import uz.uzkassa.cts.dto.file.FileDTO;
import uz.uzkassa.cts.enums.Role;
import uz.uzkassa.cts.enums.UserStatus;

import java.util.List;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class EmployeeDetailCabinetDTO extends FullNameDTO {

    String username;

    String phone;

    String email;

    CommonDTO branch;

    CommonDTO department;

    CommonDTO position;

    List<ContactListDTO> phones;

    List<ContactListDTO> emails;

    List<Role> roles;

    UserStatus status;

    FileDTO photo;
}
