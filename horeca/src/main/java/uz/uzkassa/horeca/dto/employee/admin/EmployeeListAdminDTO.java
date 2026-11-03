package uz.uzkassa.horeca.dto.employee.admin;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import uz.uzkassa.horeca.dto.base.CommonDTO;
import uz.uzkassa.horeca.enums.Role;
import uz.uzkassa.horeca.enums.UserStatus;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;

import static lombok.AccessLevel.PRIVATE;

@Setter
@Getter
@FieldDefaults(level = PRIVATE)
@NoArgsConstructor
public class EmployeeListAdminDTO implements Serializable {

    @Serial
    private static final long serialVersionUID = 456453L;

    Long id;

    String firstName;

    String lastName;

    String patronymic;

    String phone;

    Role role;

    CommonDTO department;

    CommonDTO branch;

    LocalDateTime birthDate;

    LocalDateTime joiningDate;

    UserStatus status;
}
