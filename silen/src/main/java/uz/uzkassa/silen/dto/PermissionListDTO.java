package uz.uzkassa.silen.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class PermissionListDTO extends PermissionDTO {

    List<PermissionListDTO> children = new ArrayList<>();
}
