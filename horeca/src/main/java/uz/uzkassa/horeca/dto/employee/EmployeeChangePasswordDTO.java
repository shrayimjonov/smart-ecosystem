package uz.uzkassa.horeca.dto.employee;

import jakarta.validation.constraints.Size;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

import java.io.Serial;
import java.io.Serializable;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class EmployeeChangePasswordDTO implements Serializable {

    @Serial
    private static final long serialVersionUID = 8343546L;

    @Size(min = 4, max = 50)
    String password;

    @Size(min = 4, max = 50)
    String passwordConfirmation;
}
