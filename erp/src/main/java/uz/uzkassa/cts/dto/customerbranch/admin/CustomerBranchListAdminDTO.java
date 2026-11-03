package uz.uzkassa.cts.dto.customerbranch.admin;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import uz.uzkassa.cts.dto.address.base.AddressShortDTO;
import uz.uzkassa.cts.dto.base.CommonDTO;
import uz.uzkassa.cts.dto.company.CompanyCommonDTO;
import uz.uzkassa.cts.enums.CustomerBranchStatusEnum;
import uz.uzkassa.cts.enums.OfdStatusEnum;

import java.io.Serializable;
import java.time.LocalDateTime;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CustomerBranchListAdminDTO extends CommonDTO implements Serializable {

    CompanyCommonDTO customer;

    AddressShortDTO address;

    CommonDTO activityType;

    CommonDTO apayCategory;

    String phone;

    CustomerBranchStatusEnum status;

    OfdStatusEnum ofdStatus;

    LocalDateTime createdDate;

    String cadastreNumber;

    boolean sync;

    boolean reSync;

    Long ofdId;
}
