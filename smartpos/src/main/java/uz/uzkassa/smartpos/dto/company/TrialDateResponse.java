package uz.uzkassa.smartpos.dto.company;

import lombok.Data;
import uz.uzkassa.smartpos.domain.postgres.company.TrialDateAudit;
import uz.uzkassa.smartpos.enums.TrialAuditStatus;

import java.time.LocalDateTime;

@Data
public class TrialDateResponse {
    private Long id;
    private Long companyId;
    private String companyName;
    private LocalDateTime trialDate;
    private Long createdUserId;
    private String createdUserName;
    private LocalDateTime createdTime;
    private String inn;
    private TrialAuditStatus status;
    private String firstName;
    private String lastName;


    public TrialDateResponse convert (TrialDateAudit audit) {
        TrialDateResponse trialDateAudit= new TrialDateResponse();
        trialDateAudit.setId(audit.getId());
        trialDateAudit.setCompanyId(audit.getCompanyId());
        trialDateAudit.setCompanyName(audit.getCompanyName());
        trialDateAudit.setTrialDate(audit.getTrialDate());
        trialDateAudit.setCreatedUserId(audit.getCreatedUserId());
        trialDateAudit.setCreatedUserName(audit.getCreatedBy());
        trialDateAudit.setCreatedTime(audit.getCreatedDate());
        trialDateAudit.setInn(audit.getInn());
        trialDateAudit.setStatus(audit.getStatus());
        trialDateAudit.setFirstName(audit.getUser().getFirstName());
        trialDateAudit.setLastName(audit.getUser().getLastName());
        return trialDateAudit;
    }
}
