package uz.uzkassa.horeca.dto.permission;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

import java.io.Serial;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class PermissionListDTO extends PermissionDTO implements Serializable {

    @Serial
    private static final long serialVersionUID = 348593894L;

    Long id;

    List<PermissionListDTO> children = new ArrayList<>();
}
