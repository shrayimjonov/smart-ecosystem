package uz.uzkassa.cts.dto.ofdapplication.admin;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import uz.uzkassa.cts.dto.base.CommonDTO;
import uz.uzkassa.cts.dto.company.CompanyCommonDTO;
import uz.uzkassa.cts.enums.ApplicationType;
import uz.uzkassa.cts.enums.OfdStatus;

import java.io.Serializable;
import java.time.LocalDateTime;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class OfdApplicationListAdminDTO implements Serializable {

    Long id;

    Long ofdId;

    CompanyCommonDTO customer;

    CommonDTO customerBranch;

    CommonDTO branch;

    CommonDTO device;

    CommonDTO fiscalModule;

    OfdStatus ofdStatus;

    LocalDateTime createdDate;

    ApplicationType type;

    String reason;

    String ofdFileUrl;
}
