package uz.uzkassa.horeca.dto.payload;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UserRolePermissionPayload implements Serializable {

    @Serial
    private static final long serialVersionUID = 223432L;

    Long userId;

    String role;

    List<String> permissions;
}
