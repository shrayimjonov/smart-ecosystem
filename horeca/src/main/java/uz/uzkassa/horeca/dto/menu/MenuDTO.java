package uz.uzkassa.horeca.dto.menu;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import uz.uzkassa.horeca.dto.base.CommonDTO;
import uz.uzkassa.horeca.enums.CommonStatus;

import java.io.Serial;
import java.io.Serializable;

@Getter
@Setter
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class MenuDTO extends CommonDTO implements Serializable {

    @Serial
    private static final long serialVersionUID = 893485934L;

    Long branchId;

    Long parentId;

    CommonStatus status;
}
