package uz.uzkassa.cts.dto.caseXizmat;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import uz.uzkassa.cts.dto.base.CommonDTO;
import uz.uzkassa.cts.enums.CaseType;
import uz.uzkassa.cts.enums.CaseXizmatType;
import uz.uzkassa.cts.enums.CommonStatus;
import uz.uzkassa.cts.enums.FMReportReason;

import java.io.Serializable;
import java.time.LocalDateTime;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
@NoArgsConstructor
public class CaseXizmatListAdminDTO extends CommonDTO implements Serializable {

    CaseXizmatType type;

    CaseType caseType;

    CommonStatus status;

    FMReportReason reason;

    LocalDateTime createdDate;

    Long xizmatId;

    String xizmatName;

    boolean isGrouped;
}
