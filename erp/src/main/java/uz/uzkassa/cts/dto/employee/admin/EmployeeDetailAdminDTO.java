package uz.uzkassa.cts.dto.employee.admin;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import uz.uzkassa.cts.dto.base.FullNameDTO;
import uz.uzkassa.cts.dto.contact.ContactListDTO;
import uz.uzkassa.cts.dto.file.FileDTO;
import uz.uzkassa.cts.enums.Role;

import java.util.List;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class EmployeeDetailAdminDTO extends FullNameDTO {

    String username;

    String phone;

    String email;

    List<ContactListDTO> phones;

    List<ContactListDTO> emails;

    List<Role> roles;

    FileDTO photo;
}
