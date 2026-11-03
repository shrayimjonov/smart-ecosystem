package uz.uzkassa.horeca.dto.company.admin;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import uz.uzkassa.horeca.dto.base.CommonDTO;
import uz.uzkassa.horeca.dto.company.CompanyCommonDTO;
import uz.uzkassa.horeca.enums.CommonStatus;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
@NoArgsConstructor
public class CompanyListAdminDTO extends CompanyCommonDTO implements Serializable {

    @Serial
    private static final long serialVersionUID = 8349823L;

    LocalDateTime registrationDate;

    CommonDTO owner;

    CommonStatus status;

    String phone;
}
