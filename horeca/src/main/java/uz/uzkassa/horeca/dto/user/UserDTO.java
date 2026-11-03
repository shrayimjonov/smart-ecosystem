package uz.uzkassa.horeca.dto.user;

import lombok.Getter;
import lombok.Setter;

import java.io.Serial;
import java.io.Serializable;

@Getter
@Setter
public class UserDTO implements Serializable {

    @Serial
    private static final long serialVersionUID = 489328433L;

    String tin;

    String pinfl;

    String firstName;

    String lastName;

    String patronymic;

    String login;

    String password;

    String phone;

    Long branchId;

    Long companyId;

    String role;
}
