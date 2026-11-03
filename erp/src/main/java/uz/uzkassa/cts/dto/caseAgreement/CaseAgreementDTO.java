package uz.uzkassa.cts.dto.caseAgreement;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import uz.uzkassa.cts.enums.CaseType;
import uz.uzkassa.cts.enums.CaseXizmatType;
import uz.uzkassa.cts.enums.EntityTypeEnum;
import uz.uzkassa.cts.enums.FMReportReason;

import java.io.Serializable;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
@NoArgsConstructor
public class CaseAgreementDTO implements Serializable {

    Long entityId;

    EntityTypeEnum entityType;

    String tin;

    CaseType caseType;

    FMReportReason reason;

    CaseXizmatType type;
}
