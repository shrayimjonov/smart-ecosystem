package uz.uzkassa.cts.service;

import uz.uzkassa.cts.domain.Approver;
import uz.uzkassa.cts.dto.approvalprocess.ApproverDTO;
import uz.uzkassa.cts.dto.approvalprocess.ApproverDetailDTO;
import uz.uzkassa.cts.enums.ApproverStatus;
import uz.uzkassa.cts.enums.EntityTypeEnum;

import java.util.List;

public interface ApproverService extends BaseService<Approver, Long> {

    void create(EntityTypeEnum entityType, Long entityId, List<ApproverDTO> approverDTOList);

    void updateStatus(EntityTypeEnum entityType, Long entityId, ApproverStatus status);

    ApproverDetailDTO getCurrentApprover(EntityTypeEnum entityType, Long entityId);
}
