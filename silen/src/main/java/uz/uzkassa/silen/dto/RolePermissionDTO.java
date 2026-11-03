package uz.uzkassa.silen.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;
import lombok.experimental.FieldDefaults;
import uz.uzkassa.silen.enumeration.UserRole;

import java.io.Serializable;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class RolePermissionDTO implements Serializable {

    @Size(min = 1)
    List<String> permissions;

    @NotNull
    UserRole role;
}

