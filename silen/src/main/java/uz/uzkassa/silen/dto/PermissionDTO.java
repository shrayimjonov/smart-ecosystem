package uz.uzkassa.silen.dto;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import uz.uzkassa.silen.enumeration.PermissionType;

import java.io.Serializable;

@Getter
@Setter
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class PermissionDTO implements Serializable {

    Long id;

    String name;

    String code;

    Integer position;

    boolean section;

    Long parentId;

    PermissionType permissionType;
}
