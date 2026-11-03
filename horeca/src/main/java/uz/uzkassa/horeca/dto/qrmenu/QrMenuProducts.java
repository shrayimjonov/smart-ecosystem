package uz.uzkassa.horeca.dto.qrmenu;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import uz.uzkassa.horeca.dto.file.FileDTO;
import uz.uzkassa.horeca.dto.unit.MultiUnitListDTO;

import java.util.List;

@Getter
@Setter
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class QrMenuProducts {
    Long menuId;

    FileDTO photo;

    String name;

    String description;

    List<MultiUnitListDTO> units;

    List<FileDTO> photos;
}
