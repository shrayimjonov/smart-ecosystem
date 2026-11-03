package uz.uzkassa.cts.domain.hrms;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.type.SqlTypes;
import uz.uzkassa.cts.domain.Company;
import uz.uzkassa.cts.domain.base.AbstractAuditingEntity;
import uz.uzkassa.cts.enums.ApproverCommonStatus;
import uz.uzkassa.cts.enums.EntityTypeEnum;
import uz.uzkassa.cts.interfaces.Auditable;

import java.io.Serial;
import java.time.Duration;
import java.time.LocalDateTime;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
@Table(name = "leave_request",
        indexes = {
                @Index(columnList = "leave_request_number", name = "leave_request_number_idx"),
                @Index(columnList = "from_date", name = "leave_request_from_date_idx"),
                @Index(columnList = "to_date", name = "leave_request_to_date_idx"),
                @Index(columnList = "reason_id", name = "leave_request_reason_id_idx"),
                @Index(columnList = "company_id", name = "leave_request_company_id_idx"),
                @Index(columnList = "creatorId", name = "leave_request_creator_id_idx"),
                @Index(columnList = "from_date, to_date, creator_id", name = "leave_request_from_to_dates_creator_id_idx")
        })
@Entity
@SQLDelete(sql = "UPDATE leave_request SET deleted='true' WHERE id=?")
public class LeaveRequest extends AbstractAuditingEntity implements Auditable {

    @Serial
    private static final long serialVersionUID = 6736548562643L;

    @Column(name = "leave_request_number")
    String leaveRequestNumber;

    @Column(name = "increment_number")
    Long incrementNumber;

    @Column(name = "description", nullable = false)
    @JdbcTypeCode(SqlTypes.LONGVARCHAR)
    String description;

    @Column(name = "reason_id", nullable = false)
    Long reasonId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "reason_id", updatable = false, insertable = false)
    LeaveRequestReason reason;

    @Column(name = "from_date", nullable = false)
    LocalDateTime fromDate;

    @Column(name = "to_date", nullable = false)
    LocalDateTime toDate;

    @Column(name = "duration", nullable = false)
    Integer duration; //in minutes

    @Column(name = "status")
    @Enumerated(EnumType.STRING)
    ApproverCommonStatus status = ApproverCommonStatus.DRAFT;

    @Column(name = "company_id")
    Long companyId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "company_id", updatable = false, insertable = false)
    Company company;

    public void setDescription(String description) {
        addChangeLog("description", this.description, description);
        this.description = description;
    }

    public void setFromDate(LocalDateTime fromDate) {
        addChangeLog("fromDate", this.fromDate != null ? this.fromDate.toString() : null, fromDate != null ? fromDate.toString() : null);
        this.fromDate = fromDate;
    }

    public void setToDate(LocalDateTime toDate) {
        addChangeLog("toDate", this.toDate != null ? this.toDate.toString() : null, toDate != null ? toDate.toString() : null);
        this.toDate = toDate;
    }

    public void setStatus(ApproverCommonStatus status) {
        addChangeLog("status", this.status != null ? this.status.getName() : null, status != null ? status.getName() : null);
        this.status = status;
    }

    public void setReason(LeaveRequestReason reason) {
        addChangeLog("reason", this.reason != null ? this.reason.getName() : null, reason != null ? reason.getName() : null);
        this.reason = reason;
    }

    public void setDuration(LocalDateTime fromDate, LocalDateTime toDate) {
        this.duration = Math.toIntExact(Duration.between(fromDate, toDate).toMinutes());
    }

    @Override
    public String getName() {
        return getLeaveRequestNumber();
    }

    @Override
    public Long getEntityId() {
        return getId();
    }

    @Override
    public String getEntityName() {
        return this.getName();
    }

    @Override
    public EntityTypeEnum getEntityType() {
        return EntityTypeEnum.LEAVE_REQUEST;
    }
}
