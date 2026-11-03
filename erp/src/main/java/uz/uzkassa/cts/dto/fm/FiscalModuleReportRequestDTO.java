package uz.uzkassa.cts.dto.fm;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import uz.uzkassa.cts.enums.ArrivalStatus;
import uz.uzkassa.cts.enums.FMReportReason;
import uz.uzkassa.cts.enums.SoliqTransferStatus;

import java.io.Serializable;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class FiscalModuleReportRequestDTO implements Serializable {

    Long customerId;

    String deviceSerialNumber;

    String fmSerialNumber;

    ArrivalStatus status;

    FMReportReason reason;

    SoliqTransferStatus soliqTransferStatus;
}
