package uz.uzkassa.smartpossupply.dto.permission;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class PermissionListDTO extends PermissionDTO {

    List<PermissionListDTO> children = new ArrayList<>();
}
