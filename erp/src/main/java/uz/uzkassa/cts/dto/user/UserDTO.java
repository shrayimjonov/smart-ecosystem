package uz.uzkassa.cts.dto.user;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import uz.uzkassa.cts.dto.base.FullNameDTO;
import uz.uzkassa.cts.enums.Role;

import java.io.Serializable;
import java.util.List;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UserDTO extends FullNameDTO implements Serializable {

    String username;

    String phone;

    List<Role> roles;

    String password;
}
