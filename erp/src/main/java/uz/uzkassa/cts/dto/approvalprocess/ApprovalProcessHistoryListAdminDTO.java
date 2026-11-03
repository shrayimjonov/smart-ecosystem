package uz.uzkassa.cts.dto.approvalprocess;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import uz.uzkassa.cts.dto.base.CommonDTO;
import uz.uzkassa.cts.enums.ApproverStatus;
import uz.uzkassa.cts.enums.EntityTypeEnum;

import java.io.Serializable;
import java.time.LocalDateTime;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ApprovalProcessHistoryListAdminDTO implements Serializable {

    Long id;

    EntityTypeEnum entityType;

    Long entityId;

    CommonDTO employee;

    ApproverStatus status;

    LocalDateTime createdDate;
}
