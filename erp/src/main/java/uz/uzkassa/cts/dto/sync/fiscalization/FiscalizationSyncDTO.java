package uz.uzkassa.cts.dto.sync.fiscalization;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import uz.uzkassa.cts.dto.base.CommonDTO;
import uz.uzkassa.cts.dto.company.CompanyCommonDTO;

import java.io.Serializable;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
@NoArgsConstructor
public class FiscalizationSyncDTO implements Serializable {

    Long id;

    String deviceSerial;

    String fiscalModuleSerial;

    CommonDTO customerBranch;

    CommonDTO branch;

    CompanyCommonDTO customer;

    String deviceType;
}
