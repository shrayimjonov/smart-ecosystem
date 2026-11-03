package uz.uzkassa.horeca.dto.account;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import uz.uzkassa.horeca.dto.base.CommonDTO;
import uz.uzkassa.horeca.enums.CommonStatus;

import java.io.Serial;
import java.io.Serializable;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UserCompanyDTO extends CommonDTO implements Serializable {

    @Serial
    private static final long serialVersionUID = 3437583475L;

    String tin;

    CommonStatus status;
}
