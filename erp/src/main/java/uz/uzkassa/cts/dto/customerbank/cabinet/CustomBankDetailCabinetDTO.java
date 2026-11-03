package uz.uzkassa.cts.dto.customerbank.cabinet;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import uz.uzkassa.cts.dto.base.CommonCodeDTO;
import uz.uzkassa.cts.dto.company.CompanyCommonDTO;

import java.io.Serializable;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CustomBankDetailCabinetDTO implements Serializable {

    Long id;

    CompanyCommonDTO customer;

    CommonCodeDTO bank;

    String accountNumber;

    boolean isDefault;
}
