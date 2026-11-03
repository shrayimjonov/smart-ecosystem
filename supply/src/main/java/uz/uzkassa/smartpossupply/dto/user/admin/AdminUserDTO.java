package uz.uzkassa.smartpossupply.dto.user.admin;


import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import uz.uzkassa.smartpossupply.dto.user.FullNameDTO;
import uz.uzkassa.smartpossupply.enums.Role;

/**
 * Powered by: Shuxratjon Rayimjonov
 * Date: 08.05.2023 15:56
 */
@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class AdminUserDTO extends FullNameDTO {

    Long id;

    String login;

    String password;

    String phone;

    Role role;
}
