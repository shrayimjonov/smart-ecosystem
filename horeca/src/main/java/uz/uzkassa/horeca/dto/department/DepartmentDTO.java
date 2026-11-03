package uz.uzkassa.horeca.dto.department;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import uz.uzkassa.horeca.dto.base.CommonDTO;
import uz.uzkassa.horeca.enums.DepartmentType;

import java.io.Serial;
import java.io.Serializable;
import java.util.UUID;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
@NoArgsConstructor
public class DepartmentDTO extends CommonDTO implements Serializable {

    @Serial
    private static final long serialVersionUID = 84958498L;

    Long branchId;

    DepartmentType type;

    @JsonIgnore
    UUID uniqueId;
}
