package uz.uzkassa.horeca.dto.employee.cabinet;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import uz.uzkassa.horeca.dto.base.CommonDTO;
import uz.uzkassa.horeca.dto.file.FileDTO;
import uz.uzkassa.horeca.enums.Role;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;

import static lombok.AccessLevel.PRIVATE;

@Setter
@Getter
@FieldDefaults(level = PRIVATE)
@NoArgsConstructor
public class EmployeeDetailCabinetDTO implements Serializable {

    @Serial
    private static final long serialVersionUID = 438594835L;

    Long id;

    String firstName;

    String lastName;

    String patronymic;

    String phone;

    FileDTO photo;

    Role role;

    CommonDTO department;

    CommonDTO branch;

    LocalDateTime joiningDate;

    LocalDateTime birthDate;
}
