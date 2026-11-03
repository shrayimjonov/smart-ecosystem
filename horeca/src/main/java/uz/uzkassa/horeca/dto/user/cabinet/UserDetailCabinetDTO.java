package uz.uzkassa.horeca.dto.user.cabinet;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import uz.uzkassa.horeca.dto.base.CommonDTO;
import uz.uzkassa.horeca.enums.Role;
import uz.uzkassa.horeca.enums.UserStatus;

import java.io.Serial;
import java.io.Serializable;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UserDetailCabinetDTO implements Serializable {

    @Serial
    private static final long serialVersionUID = 5893485943L;

    Long id;

    String tin;

    String pinfl;

    String firstName;

    String lastName;

    String patronymic;

    String login;

    String phone;

    Role role;

    UserStatus status;

    CommonDTO branch;
}
