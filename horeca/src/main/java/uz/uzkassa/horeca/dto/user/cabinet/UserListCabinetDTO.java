package uz.uzkassa.horeca.dto.user.cabinet;

import lombok.Getter;
import lombok.Setter;
import uz.uzkassa.horeca.dto.base.CommonDTO;
import uz.uzkassa.horeca.enums.Role;
import uz.uzkassa.horeca.enums.UserStatus;

import java.io.Serial;
import java.io.Serializable;

@Getter
@Setter
public class UserListCabinetDTO implements Serializable {

    @Serial
    private static final long serialVersionUID = 4589348543L;

    Long id;

    String tin;

    String pinfl;

    String firstName;

    String lastName;

    String patronymic;

    String login;

    Role role;

    UserStatus status;

    CommonDTO branch;
}
