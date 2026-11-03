package uz.uzkassa.cts.dto.customeremployee;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import uz.uzkassa.cts.dto.base.FullNameDTO;
import uz.uzkassa.cts.enums.Role;

import java.io.Serializable;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CustomerEmployeeDTO extends FullNameDTO implements Serializable {

    Long customerId;

    Role role;
}
