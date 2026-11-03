package uz.uzkassa.cts.dto.customerbranchapplication.admin;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import uz.uzkassa.cts.dto.base.CommonDTO;
import uz.uzkassa.cts.dto.company.CompanyCommonDTO;
import uz.uzkassa.cts.enums.CustomerBranchApplicationStatus;

import java.io.Serializable;
import java.time.LocalDateTime;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
@NoArgsConstructor
public class CustomerBranchApplicationListAdminDTO implements Serializable {

    Long id;

    CommonDTO manager;

    CommonDTO customerBranch;

    CompanyCommonDTO customer;

    CommonDTO region;

    CommonDTO district;

    CommonDTO reason;

    CustomerBranchApplicationStatus status;

    LocalDateTime createdDate;
}
