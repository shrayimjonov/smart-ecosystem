package uz.uzkassa.smartpossupply.dto.base;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import uz.uzkassa.smartpossupply.enums.Role;

import java.io.Serializable;
import java.util.Objects;

/**
 * Powered by: Shuxratjon Rayimjonov
 * Date: 17.04.2025
 */
@Getter
@Setter
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PROTECTED)
public class UserCommonDTO implements Serializable {

    Long id;

    String name;

    Role role;

    public UserCommonDTO(Long id, String name, Role role) {
        this.id = id;
        this.name = name;
        this.role = role;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof UserCommonDTO)) {
            return false;
        }
        return Objects.equals(id, ((UserCommonDTO) o).id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }
}
