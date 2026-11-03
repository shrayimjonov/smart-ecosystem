package uz.uzkassa.silen.dto.filter;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.FieldDefaults;
import uz.uzkassa.silen.enumeration.UserRole;

import java.util.EnumSet;

@Getter
@Setter
@ToString
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UserFilter extends Filter {
    String login;

    UserRole role;

    EnumSet<UserRole> roles;

    String supervisorId;

    boolean supervisorNull = false;

    Boolean employee;

    Boolean accounts;

    String pinfl;

}
