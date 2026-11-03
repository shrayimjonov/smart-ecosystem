package uz.uzkassa.horeca.dto.menu.mobile;

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
public class MenuListMobileDTO extends CommonDTO implements Serializable {

    @Serial
    private static final long serialVersionUID = 893485934L;

    FileDTO photo;

    CommonDTO parent;

    boolean hasChild;

    CommonStatus status;
}
