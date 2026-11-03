package uz.uzkassa.cts.dto.employee.base;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import uz.uzkassa.cts.dto.base.FullNameDTO;
import uz.uzkassa.cts.dto.contact.ContactDTO;
import uz.uzkassa.cts.enums.Role;

import java.time.LocalDate;
import java.util.List;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class EmployeeDTO extends FullNameDTO {

    String username;

    String password;

    LocalDate birthday;

    List<ContactDTO> phones;

    List<ContactDTO> emails;

    List<Role> roles;
}
