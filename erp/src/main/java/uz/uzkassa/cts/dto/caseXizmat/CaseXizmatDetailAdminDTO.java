package uz.uzkassa.cts.dto.caseXizmat;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import uz.uzkassa.cts.dto.base.CommonDTO;
import uz.uzkassa.cts.enums.CaseType;
import uz.uzkassa.cts.enums.CaseXizmatType;
import uz.uzkassa.cts.enums.CommonStatus;
import uz.uzkassa.cts.enums.FMReportReason;

import java.io.Serializable;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CaseXizmatDetailAdminDTO extends CommonDTO implements Serializable {

    CommonDTO xizmat;

    CaseXizmatType type;

    CaseType caseType;

    FMReportReason reason;

    CommonStatus status;

    boolean isGrouped;
}
