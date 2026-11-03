package uz.uzkassa.cts.dto.employee;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import uz.uzkassa.cts.dto.base.CommonDTO;

/**
 * Created date time: 13.02.2025 15:35
 */

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class EmployeeListLoginDTO extends CommonDTO {

    String login;
}
