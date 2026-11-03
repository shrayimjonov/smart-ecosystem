package uz.uzkassa.horeca.dto.employee.admin;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

import java.io.Serial;
import java.io.Serializable;

import static lombok.AccessLevel.PRIVATE;

@Setter
@Getter
@FieldDefaults(level = PRIVATE)
@NoArgsConstructor
public class EmployeeAdminDTO implements Serializable {

    @Serial
    private static final long serialVersionUID = 346456L;

    Long id;

    String firstName;

    String lastName;

    String patronymic;

    String role;

    String login;

    String password;

    Long departmentId;

    Long companyId;

    Long branchId;
}
