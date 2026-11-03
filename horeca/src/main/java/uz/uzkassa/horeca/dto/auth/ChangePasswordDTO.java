package uz.uzkassa.horeca.dto.auth;

import jakarta.validation.constraints.Size;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import uz.uzkassa.horeca.dto.employee.EmployeeChangePasswordDTO;

import java.io.Serial;
import java.io.Serializable;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ChangePasswordDTO extends EmployeeChangePasswordDTO implements Serializable {

    @Serial
    private static final long serialVersionUID = 845724546L;

    @Size(min = 6, max = 6)
    String activationKey;

    @Size(min = 36, max = 36)
    String secretKey;
}
