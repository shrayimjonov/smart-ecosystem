package uz.uzkassa.horeca.dto.menu;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import uz.uzkassa.horeca.dto.base.CommonDTO;
import uz.uzkassa.horeca.dto.file.FileDTO;
import uz.uzkassa.horeca.dto.unit.MultiUnitListDTO;
import uz.uzkassa.horeca.dto.unit.TerminalMultiUnitDTO;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;

/**
 * Powered by: Shuxratjon Rayimjonov
 * Date: 16.08.2024
 */
@Getter
@Setter
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class TerminalMenuItemDetailDTO extends CommonDTO implements Serializable {

    @Serial
    private static final long serialVersionUID = 8938533L;

    CommonDTO product;

    FileDTO photo;

    CommonDTO menu;

    TerminalMultiUnitDTO unit;
}
