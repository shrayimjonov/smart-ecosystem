package uz.uzkassa.cts.dto.customer;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import uz.uzkassa.cts.enums.BusinessType;
import uz.uzkassa.cts.enums.CustomerAppType;

import java.io.Serializable;
import java.time.LocalDateTime;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
@NoArgsConstructor
public class CompanyCustomerListDTO implements Serializable {

    Long id;

    String name;

    String companyName;

    String tin;

    String oldTin;

    String activityType;

    String phone;

    boolean sendGeolocation;

    boolean whiteList;

    BusinessType businessType;

    Long branchId;

    String branch;

    LocalDateTime createdDate;

    String creator;

    String bankName;

    Long deviceTotal;

    Long fiscalModuleTotal;

    CustomerAppType appType;
}
