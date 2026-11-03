package uz.uzkassa.cts.dto.customer.admin;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import uz.uzkassa.cts.dto.address.base.BaseAddressDetailDTO;
import uz.uzkassa.cts.dto.banktariff.BankTariffDetailDTO;
import uz.uzkassa.cts.dto.base.CommonCodeDTO;
import uz.uzkassa.cts.dto.base.CommonDTO;
import uz.uzkassa.cts.dto.company.CompanyCommonDTO;
import uz.uzkassa.cts.dto.contact.ContactListDTO;
import uz.uzkassa.cts.dto.vat.VatItemDTO;
import uz.uzkassa.cts.enums.BusinessType;
import uz.uzkassa.cts.enums.CustomerAppType;
import uz.uzkassa.cts.enums.CustomerLegalStatus;
import uz.uzkassa.cts.enums.CustomerStatus;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CustomerDetailAdminDTO extends CompanyCommonDTO implements Serializable {

    String companyName;

    String oldTin;

    String oked;

    CustomerAppType appType;

    CommonDTO activityType;

    CommonDTO parentActivityType;

    BusinessType businessType;

    BaseAddressDetailDTO address;

    VatItemDTO vat;

    CommonDTO manager;

    CustomerStatus status;

    CustomerLegalStatus legalStatus;

    BankTariffDetailDTO bankTariff;

    CommonCodeDTO bank;

    String accountNumber;

    String apayTariffId;

    List<ContactListDTO> phones;

    List<CommonDTO> branches;

    LocalDateTime createdDate;

    String activationCode;
}
