package uz.uzkassa.cts.repository;

import org.springframework.stereotype.Repository;
import uz.uzkassa.cts.domain.approval.process.ApprovalProcessStep;

import java.util.List;

@Repository
public interface ApprovalProcessStepRepository extends BaseRepository<ApprovalProcessStep, Long> {

    void deleteAllByApprovalProcessIdIn(List<Long> approvalProcessIds);
}
