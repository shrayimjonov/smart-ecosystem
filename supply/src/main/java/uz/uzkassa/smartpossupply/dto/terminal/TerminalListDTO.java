package uz.uzkassa.smartpossupply.dto.terminal;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import uz.uzkassa.smartpossupply.dto.base.CommonDTO;
import uz.uzkassa.smartpossupply.dto.company.CompanyBaseDTO;
import uz.uzkassa.smartpossupply.enums.CommonStatus;
import uz.uzkassa.smartpossupply.enums.LauncherStatus;

import java.io.Serializable;

@Getter
@Setter
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class TerminalListDTO implements Serializable {

    Long id;

    String serialNumber;

    String fiscalModuleSerialNumber;

    CompanyBaseDTO company;

    CommonDTO branch;

    CommonStatus status;

    LauncherStatus launcherStatus;
}
