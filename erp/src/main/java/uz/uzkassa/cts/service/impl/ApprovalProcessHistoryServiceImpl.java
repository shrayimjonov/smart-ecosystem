package uz.uzkassa.cts.service.impl;

import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uz.uzkassa.cts.domain.approval.process.ApprovalProcessHistory;
import uz.uzkassa.cts.dto.approvalprocess.ApprovalProcessHistoryDTO;
import uz.uzkassa.cts.dto.approvalprocess.ApprovalProcessHistoryListAdminDTO;
import uz.uzkassa.cts.enums.EntityTypeEnum;
import uz.uzkassa.cts.repository.ApprovalProcessHistoryRepository;
import uz.uzkassa.cts.service.ApprovalProcessHistoryService;

import java.util.List;

@Service
@Transactional
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public class ApprovalProcessHistoryServiceImpl extends BaseServiceImpl<ApprovalProcessHistory, Long> implements ApprovalProcessHistoryService {

    ApprovalProcessHistoryRepository approvalProcessHistoryRepository;

    public ApprovalProcessHistoryServiceImpl(ApprovalProcessHistoryRepository approvalProcessHistoryRepository) {
        super(approvalProcessHistoryRepository);
        this.approvalProcessHistoryRepository = approvalProcessHistoryRepository;
    }

    @Override
    public Long create(ApprovalProcessHistoryDTO approvalProcessHistoryDTO) {
        ApprovalProcessHistory approvalProcessHistory = new ApprovalProcessHistory();
        approvalProcessHistory.setEntityId(approvalProcessHistoryDTO.getEntityId());
        approvalProcessHistory.setEntityType(approvalProcessHistoryDTO.getEntityType());
        approvalProcessHistory.setApproverId(approvalProcessHistoryDTO.getApproverId());
        approvalProcessHistory.setStatus(approvalProcessHistoryDTO.getStatus());
        return approvalProcessHistoryRepository.save(approvalProcessHistory).getId();
    }

    @Override
    public List<ApprovalProcessHistoryListAdminDTO> getList(Long entityId, EntityTypeEnum entityType) {
        List<ApprovalProcessHistory> resultList = approvalProcessHistoryRepository.findAllByEntityIdAndEntityType(entityId, entityType);
        return resultList
                .stream()
                .map(approvalProcessHistory -> {
                    ApprovalProcessHistoryListAdminDTO approvalProcessHistoryListAdminDTO = new ApprovalProcessHistoryListAdminDTO();
                    approvalProcessHistoryListAdminDTO.setId(approvalProcessHistory.getId());
                    approvalProcessHistoryListAdminDTO.setEntityId(approvalProcessHistory.getEntityId());
                    approvalProcessHistoryListAdminDTO.setEntityType(approvalProcessHistory.getEntityType());
                    approvalProcessHistoryListAdminDTO.setEmployee(approvalProcessHistory.getApprover() != null ? approvalProcessHistory.getApprover().toCommonDTO() : null);
                    approvalProcessHistoryListAdminDTO.setStatus(approvalProcessHistory.getStatus());
                    approvalProcessHistoryListAdminDTO.setCreatedDate(approvalProcessHistory.getCreatedDate());
                    return approvalProcessHistoryListAdminDTO;
                })
                .toList();
    }
}
