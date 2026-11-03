package uz.uzkassa.cts.dto.approvalprocess;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import uz.uzkassa.cts.enums.ApproverStatus;
import uz.uzkassa.cts.enums.EntityTypeEnum;

import java.io.Serializable;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ApprovalProcessHistoryDTO implements Serializable {

    Long entityId;

    EntityTypeEnum entityType;

    Long approverId;

    ApproverStatus status;
}
