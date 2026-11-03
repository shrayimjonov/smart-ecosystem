package uz.uzkassa.silen.dto.mq;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.io.Serializable;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UserRolePermissionPayload implements Serializable {

    String userId;

    List<Long> permissionIds;
}
