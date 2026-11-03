package uz.uzkassa.horeca.dto.sync;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import uz.uzkassa.horeca.dto.base.CommonDTO;
import uz.uzkassa.horeca.dto.company.CompanyCommonDTO;

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

    CompanyCommonDTO customer;

    CommonDTO branch;
}
