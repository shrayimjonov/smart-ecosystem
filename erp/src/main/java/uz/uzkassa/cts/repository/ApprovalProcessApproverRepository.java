package uz.uzkassa.cts.repository;

import org.springframework.stereotype.Repository;
import uz.uzkassa.cts.domain.approval.process.ApprovalProcessApprover;

@Repository
public interface ApprovalProcessApproverRepository extends BaseRepository<ApprovalProcessApprover, Long> {

    void deleteAllByApprovalProcessStepId(Long approvalProcessStepId);
}
