package uz.uzkassa.cts.dto.caseXizmat;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import uz.uzkassa.cts.dto.base.CommonDTO;
import uz.uzkassa.cts.enums.CaseType;
import uz.uzkassa.cts.enums.CaseXizmatType;
import uz.uzkassa.cts.enums.FMReportReason;

import java.io.Serializable;

@Setter
@Getter
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CaseXizmatDTO extends CommonDTO implements Serializable {

    Long xizmatId;

    String xizmatName;

    CaseXizmatType type;

    CaseType caseType;

    FMReportReason reason;

    boolean isGrouped = false;
}
