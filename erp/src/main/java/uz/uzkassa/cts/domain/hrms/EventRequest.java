package uz.uzkassa.cts.domain.hrms;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import uz.uzkassa.cts.domain.Company;
import uz.uzkassa.cts.domain.User;
import uz.uzkassa.cts.domain.base.AbstractAuditingEntity;
import uz.uzkassa.cts.enums.ApproverCommonStatus;
import uz.uzkassa.cts.enums.EntityTypeEnum;
import uz.uzkassa.cts.interfaces.Auditable;

import java.io.Serial;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "event_request",
        indexes = {
                @Index(columnList = "approver_id", name = "event_request_approver_id_idx"),
                @Index(columnList = "status", name = "event_request_status_idx"),
                @Index(columnList = "event_request_number", name = "event_request_number_idx"),
                @Index(columnList = "company_id", name = "event_request_company_id_idx")
        })
@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class EventRequest extends AbstractAuditingEntity implements Auditable {

    @Serial
    private static final long serialVersionUID = 32256884225L;

    @Column(name = "event_request_number")
    String eventRequestNumber;

    @Column(name = "increment_number")
    Long incrementNumber;

    @NotNull
    @Column(name = "subject", nullable = false)
    String subject;

    @Column(name = "status")
    @Enumerated(EnumType.STRING)
    ApproverCommonStatus status = ApproverCommonStatus.DRAFT;

    @NotNull
    @Column(name = "from_date", nullable = false)
    LocalDateTime fromDate;

    @NotNull
    @Column(name = "toDate", nullable = false)
    LocalDateTime toDate;

    @NotNull
    @Column(name = "approver_id", nullable = false)
    Long approverId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "approver_id", updatable = false, insertable = false)
    User approver;

    @NotNull
    @Column(name = "member_count", nullable = false)
    Integer memberCount;

    @NotNull
    @Column(name = "room", nullable = false)
    String room;

    @OneToMany(mappedBy = "eventRequest", cascade = CascadeType.ALL, orphanRemoval = true)
    @OrderBy("id")
    List<EventRequestItem> items = new ArrayList<>();

    @Column(name = "company_id")
    Long companyId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "company_id", updatable = false, insertable = false)
    Company company;

    public void addItem(EventRequestItem item) {
        item.setEventRequest(this);
        item.setEventRequestId(this.id);
        items.add(item);
    }

    public void setStatus(ApproverCommonStatus status) {
        addChangeLog("status", this.status != null ? this.status.getName() : null, status != null ? status.getName() : null);
        this.status = status;
    }

    public void setFromDate(LocalDateTime fromDate) {
        addChangeLog("fromDate", this.fromDate != null ? String.valueOf(this.fromDate) : null, fromDate != null ? String.valueOf(fromDate) : null);
        this.fromDate = fromDate;
    }

    public void setToDate(LocalDateTime toDate) {
        addChangeLog("toDate", this.toDate != null ? String.valueOf(this.toDate) : null, toDate != null ? String.valueOf(toDate) : null);
        this.toDate = toDate;
    }

    public void setApprover(User approver) {
        addChangeLog("approver", this.approver != null ? this.approver.getName() : null, approver != null ? approver.getName() : null);
        this.approver = approver;
    }

    public void setCompany(Company company) {
        addChangeLog("company", this.company != null ? this.company.getName() : null, company != null ? company.getName() : null);
        this.company = company;
    }

    public void setMemberCount(Integer memberCount) {
        addChangeLog("memberCount", this.memberCount != null ? String.valueOf(this.memberCount) : null, memberCount != null ? String.valueOf(memberCount) : null);
        this.memberCount = memberCount;
    }

    public void setRoom(String room) {
        addChangeLog("room", this.room, room);
        this.room = room;
    }

    @Override
    public Long getEntityId() {
        return this.id;
    }

    @Override
    public String getName() {
        return this.getEventRequestNumber();
    }

    @Override
    public EntityTypeEnum getEntityType() {
        return EntityTypeEnum.EVENT_REQUEST;
    }
}
