package uz.uzkassa.horeca.dto.department;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import uz.uzkassa.horeca.dto.base.CommonDTO;
import uz.uzkassa.horeca.enums.CommonStatus;
import uz.uzkassa.horeca.enums.DepartmentType;

import java.io.Serial;
import java.io.Serializable;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
@NoArgsConstructor
public class DepartmentListDTO extends CommonDTO implements Serializable {

    @Serial
    private static final long serialVersionUID = 83492384L;

    DepartmentType type;

    CommonDTO branch;

    CommonStatus status;
}
