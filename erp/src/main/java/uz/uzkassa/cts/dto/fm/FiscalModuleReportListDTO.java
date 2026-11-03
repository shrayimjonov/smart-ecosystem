package uz.uzkassa.cts.dto.fm;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import uz.uzkassa.cts.dto.base.CommonDTO;
import uz.uzkassa.cts.dto.customer.CustomerShortDTO;
import uz.uzkassa.cts.enums.ArrivalStatus;
import uz.uzkassa.cts.enums.CaseType;
import uz.uzkassa.cts.enums.FMReportReason;
import uz.uzkassa.cts.enums.SoliqTransferStatus;

import java.io.Serializable;
import java.time.LocalDateTime;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
@NoArgsConstructor
public class FiscalModuleReportListDTO implements Serializable {

    Long id;
    CustomerShortDTO customer;
    String deviceSerialNumber;
    String fmSerialNumber;
    String statusReason;
    ArrivalStatus arrivalStatus;
    LocalDateTime arrivalDate;
    CommonDTO operator;
    CommonDTO region;
    LocalDateTime createdDate;
    String lastModifier;
    CaseType caseType;
    Long caseId;
    FMReportReason reason;
    SoliqTransferStatus soliqTransferStatus;
}
