package uz.uzkassa.cts.service;

import uz.uzkassa.cts.domain.approval.process.ApprovalProcessHistory;
import uz.uzkassa.cts.dto.approvalprocess.ApprovalProcessHistoryDTO;
import uz.uzkassa.cts.dto.approvalprocess.ApprovalProcessHistoryListAdminDTO;
import uz.uzkassa.cts.enums.EntityTypeEnum;

import java.util.List;

public interface ApprovalProcessHistoryService extends BaseService<ApprovalProcessHistory, Long> {

    Long create(ApprovalProcessHistoryDTO approvalProcessHistoryDTO);

    List<ApprovalProcessHistoryListAdminDTO> getList(Long entityId, EntityTypeEnum entityType);
}
