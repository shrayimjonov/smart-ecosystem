package uz.uzkassa.smartpossupply.dto.company;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import uz.uzkassa.smartpossupply.dto.address.AddressDetailDTO;
import uz.uzkassa.smartpossupply.dto.companybank.CompanyBankDetailDTO;
import uz.uzkassa.smartpossupply.dto.user.OwnerDTO;
import uz.uzkassa.smartpossupply.enums.BusinessType;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
@NoArgsConstructor
public class CheckTinResultDTO extends CompanyBaseDTO {

    BusinessType businessType;

    AddressDetailDTO address;

    OwnerDTO ownerDTO;

    String phone;

    String gcp;

    String turonToken;

    String omsId;

    CompanyBankDetailDTO bankDetail;
}
