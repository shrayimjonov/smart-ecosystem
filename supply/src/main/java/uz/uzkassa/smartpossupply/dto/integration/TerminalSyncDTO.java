package uz.uzkassa.smartpossupply.dto.integration;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import uz.uzkassa.smartpossupply.dto.base.CommonDTO;
import uz.uzkassa.smartpossupply.dto.company.CompanyBaseDTO;

import java.io.Serializable;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
@NoArgsConstructor
public class TerminalSyncDTO implements Serializable {

    Long id;

    String deviceSerial;

    String fiscalModuleSerial;

    CommonDTO customerBranch;

    CommonDTO branch;

    CompanyBaseDTO customer;

    String deviceType;
}
