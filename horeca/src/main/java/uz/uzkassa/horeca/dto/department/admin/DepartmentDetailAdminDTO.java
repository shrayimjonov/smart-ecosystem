package uz.uzkassa.horeca.dto.department.admin;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import uz.uzkassa.horeca.dto.base.CommonDTO;

import java.io.Serial;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
@NoArgsConstructor
public class DepartmentDetailAdminDTO extends CommonDTO {

    @Serial
    private static final long serialVersionUID = 34878L;

    CommonDTO branch;
}
