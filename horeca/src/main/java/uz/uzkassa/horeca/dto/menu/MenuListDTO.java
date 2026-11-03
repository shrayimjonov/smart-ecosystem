package uz.uzkassa.horeca.dto.menu;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import uz.uzkassa.horeca.dto.base.CommonDTO;
import uz.uzkassa.horeca.dto.file.FileDTO;
import uz.uzkassa.horeca.enums.CommonStatus;

import java.io.Serial;
import java.io.Serializable;

@Getter
@Setter
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class MenuListDTO extends CommonDTO implements Serializable {

    FileDTO photo;

    CommonDTO parent;

    CommonStatus status;

    @Serial
    private static final long serialVersionUID = 893485934L;
}
