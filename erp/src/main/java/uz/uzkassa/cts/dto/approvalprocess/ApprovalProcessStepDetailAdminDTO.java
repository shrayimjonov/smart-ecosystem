package uz.uzkassa.cts.dto.approvalprocess;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import uz.uzkassa.cts.enums.ApproveAction;

import java.io.Serializable;
import java.util.List;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ApprovalProcessStepDetailAdminDTO implements Serializable {

    Long id;

    Integer step = 1;

    ApproveAction approveAction;

    ApproveAction rejectAction;

    List<ApprovalProcessApproverDetailAdminDTO> approvers;
}
