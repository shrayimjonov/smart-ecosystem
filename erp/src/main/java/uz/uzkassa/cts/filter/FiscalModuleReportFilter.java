package uz.uzkassa.cts.filter;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import uz.uzkassa.cts.enums.ArrivalStatus;
import uz.uzkassa.cts.enums.CaseType;
import uz.uzkassa.cts.enums.FMReportReason;

import java.time.LocalDate;
import java.util.List;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class FiscalModuleReportFilter extends BaseFilter {

    LocalDate arrivalFrom;
    LocalDate arrivalTo;
    ArrivalStatus arrivalStatus;
    FMReportReason reason;
    CaseType caseType;
    List<Long> caseCreatorIds;
    List<Long> regionIds;
}
