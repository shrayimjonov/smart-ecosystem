package uz.uzkassa.cts.dto.company.admin;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import uz.uzkassa.cts.dto.address.base.AddressShortDTO;
import uz.uzkassa.cts.dto.company.CompanyCommonDTO;
import uz.uzkassa.cts.enums.CommonStatus;

import java.io.Serializable;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
@NoArgsConstructor
public class CompanyListAdminDTO extends CompanyCommonDTO implements Serializable {

    String phone;

    AddressShortDTO address;

    CommonStatus status;
}
