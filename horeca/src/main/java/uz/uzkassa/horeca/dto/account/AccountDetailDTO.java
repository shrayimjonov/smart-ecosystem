package uz.uzkassa.horeca.dto.account;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import uz.uzkassa.horeca.dto.base.CommonDTO;
import uz.uzkassa.horeca.dto.company.CompanyCommonDTO;
import uz.uzkassa.horeca.dto.file.FileDTO;
import uz.uzkassa.horeca.enums.Role;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Set;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class AccountDetailDTO extends AccountProfileDTO implements Serializable {

    @Serial
    private static final long serialVersionUID = 8738473L;

    String phone;

    FileDTO profileImage;

    Role role;

    String pinfl;

    CompanyCommonDTO company;

    CommonDTO branch;

    CommonDTO department;

    LocalDateTime birthDate;

    LocalDateTime joiningDate;

    Set<String> permissions;
}
