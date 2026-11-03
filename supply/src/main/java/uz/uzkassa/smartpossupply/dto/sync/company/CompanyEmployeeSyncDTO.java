package uz.uzkassa.smartpossupply.dto.sync.company;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import uz.uzkassa.smartpossupply.dto.user.FullNameDTO;

import java.io.Serializable;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CompanyEmployeeSyncDTO extends FullNameDTO implements Serializable {

    Long id;

    String phone;

    String username;
}
