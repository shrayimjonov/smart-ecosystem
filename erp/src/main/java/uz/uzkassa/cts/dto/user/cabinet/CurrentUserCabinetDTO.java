package uz.uzkassa.cts.dto.user.cabinet;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import uz.uzkassa.cts.dto.base.CommonDTO;
import uz.uzkassa.cts.enums.Role;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
@NoArgsConstructor
public class CurrentUserCabinetDTO implements Serializable {

    Long id;

    String firstName;

    String lastName;

    CommonDTO branch;

    CommonDTO company;

    List<Role> roles = new ArrayList<>();

    @JsonIgnore
    public void addRole(Role role) {
        roles.add(role);
    }
}
