package uz.uzkassa.cts.dto.company.base;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import uz.uzkassa.cts.dto.address.base.BaseAddressDetailDTO;
import uz.uzkassa.cts.dto.base.CommonCodeDTO;
import uz.uzkassa.cts.dto.base.CommonDTO;
import uz.uzkassa.cts.dto.company.CompanyCommonDTO;

import java.io.Serializable;

@Getter
@Setter
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CompanyCheckDTO extends CompanyCommonDTO implements Serializable {

    BaseAddressDetailDTO address;

    String oked;

    CommonCodeDTO bank;

    String accountNumber;

    CommonDTO status;

    CompanyCommonDTO director;

    String accountant;
}
