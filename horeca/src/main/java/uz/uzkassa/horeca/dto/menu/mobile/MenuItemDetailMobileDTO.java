package uz.uzkassa.horeca.dto.menu.mobile;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import uz.uzkassa.horeca.dto.base.CommonDTO;
import uz.uzkassa.horeca.dto.file.FileDTO;
import uz.uzkassa.horeca.dto.unit.MultiUnitListDTO;
import uz.uzkassa.horeca.enums.CommonStatus;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;

/**
 * Powered by: Shuxratjon Rayimjonov
 * Date: 16.02.2024
 */
@Getter
@Setter
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class MenuItemDetailMobileDTO extends CommonDTO implements Serializable {

    @Serial
    private static final long serialVersionUID = 8938533L;

    CommonDTO product;

    FileDTO photo;

    CommonDTO menu;

    List<MultiUnitListDTO> units;

    List<FileDTO> photos;

    CommonStatus status;
}
