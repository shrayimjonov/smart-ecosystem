package uz.uzkassa.cts.dto.customer.admin;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import uz.uzkassa.cts.dto.address.base.AddressShortDTO;
import uz.uzkassa.cts.dto.base.CommonDTO;
import uz.uzkassa.cts.dto.company.CompanyCommonDTO;
import uz.uzkassa.cts.enums.BusinessType;
import uz.uzkassa.cts.enums.CustomerStatus;

import java.io.Serializable;
import java.time.LocalDateTime;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CustomerListAdminDTO extends CompanyCommonDTO implements Serializable {

    String companyName;

    CommonDTO activityType;

    BusinessType businessType;

    String phone;

    CommonDTO branch;

    CommonDTO creator;

    LocalDateTime createdDate;

    AddressShortDTO address;

    CustomerStatus status;

    boolean whiteList;

    CommonDTO manager;

    long daysFromPayment;
}
