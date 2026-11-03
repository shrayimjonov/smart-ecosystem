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
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class DepartmentListAdminDTO extends CommonDTO {

    @Serial
    private static final long serialVersionUID = 3355771L;

    CommonDTO branch;
}
