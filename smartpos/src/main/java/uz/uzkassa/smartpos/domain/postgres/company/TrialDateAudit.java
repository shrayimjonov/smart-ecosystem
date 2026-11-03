package uz.uzkassa.smartpos.domain.postgres.company;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import uz.uzkassa.smartpos.domain.postgres.base.audit.AbstractAuditingEntity;
import uz.uzkassa.smartpos.domain.postgres.user.User;
import uz.uzkassa.smartpos.enums.TrialAuditStatus;

import java.io.Serializable;
import java.time.LocalDateTime;

@Entity
@Table(name = "trial_date_audit")
@Getter
@Setter
public class TrialDateAudit extends AbstractAuditingEntity implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Long companyId;
    private String companyName;
    private LocalDateTime trialDate;
    @Column(name ="created_user_id")
    private Long createdUserId;
    @ManyToOne
    @JoinColumn(name = "created_user_id",insertable=false, updatable=false , foreignKey = @ForeignKey(ConstraintMode.NO_CONSTRAINT))
    private User user;

    private String inn;
    @Enumerated(EnumType.STRING)
    private TrialAuditStatus status;
}
