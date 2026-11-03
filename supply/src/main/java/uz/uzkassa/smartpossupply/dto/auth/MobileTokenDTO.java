package uz.uzkassa.smartpossupply.dto.auth;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import uz.uzkassa.smartpossupply.enums.Role;

import java.util.Set;

/**
 * Powered by: Shuxratjon Rayimjonov
 * Date: 04.03.2024
 */

@Getter
@Setter
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class MobileTokenDTO extends TokenDTO {

    Set<Role> roles;

    public MobileTokenDTO(String accessToken, Set<Role> role) {
        super(accessToken);
        this.roles = role;
    }
}
