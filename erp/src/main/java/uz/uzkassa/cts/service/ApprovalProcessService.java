package uz.uzkassa.cts.service;

import uz.uzkassa.cts.domain.approval.process.ApprovalProcess;
import uz.uzkassa.cts.dto.approvalprocess.ApprovalProcessDTO;
import uz.uzkassa.cts.dto.approvalprocess.ApprovalProcessDetailAdminDTO;
import uz.uzkassa.cts.enums.EntityTypeEnum;

public interface ApprovalProcessService extends BaseService<ApprovalProcess, Long> {

    Long create(ApprovalProcessDTO approvalProcessDTO);

    Long update(Long id, ApprovalProcessDTO approvalProcessDTO);

    ApprovalProcessDetailAdminDTO getByEntityTypeAndCompany(EntityTypeEnum entityType, Long companyId);
}
