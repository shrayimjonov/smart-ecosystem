package uz.uzkassa.cts.dto.report;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import uz.uzkassa.cts.dto.base.CommonDTO;

import java.io.Serializable;
import java.math.BigInteger;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
@NoArgsConstructor
public class StatisticsByBranchReportDTO implements Serializable {

    CommonDTO branch;

    BigInteger customerTotal;

    BigInteger customerBranchTotal;

    BigInteger deviceTotal;

    BigInteger freeDeviceTotal;

    BigInteger registeredDeviceTotal;

    BigInteger transferredDeviceTotal;

    BigInteger fiscalModuleTotal;

    BigInteger freeFiscalModuleTotal;

    BigInteger registeredFiscalModuleTotal;

    BigInteger transferredFiscalModuleTotal;

    BigInteger fiscalizationTotal;

    BigInteger applicationTotal;
}
