package uz.uzkassa.cts.domain.hrms;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;
import uz.uzkassa.cts.domain.Company;
import uz.uzkassa.cts.domain.base.AbstractAuditingEntity;
import uz.uzkassa.cts.enums.EntityTypeEnum;
import uz.uzkassa.cts.enums.EventStatus;
import uz.uzkassa.cts.interfaces.Auditable;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Created date time: 26.12.2024 16:01
 */

@Entity
@Table(name = "event",
        indexes = {
                @Index(columnList = "event_request_id", name = "event_event_request_id_idx"),
                @Index(columnList = "company_id", name = "event_company_id_idx"),
                @Index(columnList = "status", name = "event_status_idx")
        })
@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Event extends AbstractAuditingEntity implements Auditable {

    @Serial
    private static final long serialVersionUID = 1256884225L;

    @NotNull
    @Column(name = "subject", nullable = false)
    String subject;

    @Column(name = "description")
    @JdbcTypeCode(SqlTypes.LONGVARCHAR)
    String description;

    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    EventStatus status = EventStatus.ACTIVE;

    @NotNull
    @Column(name = "from_date", nullable = false)
    LocalDateTime fromDate;

    @NotNull
    @Column(name = "to_date", nullable = false)
    LocalDateTime toDate;

    @Column(name = "guest")
    @JdbcTypeCode(SqlTypes.LONGVARCHAR)
    String guest;

    @Column(name = "event_request_id")
    Long eventRequestId;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "event_request_id", updatable = false, insertable = false)
    EventRequest eventRequest;

    @Column(name = "company_id", nullable = false)
    Long companyId;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "company_id", updatable = false, insertable = false)
    Company company;

    @OneToMany(mappedBy = "event", cascade = CascadeType.ALL, orphanRemoval = true)
    List<EventShare> shares = new ArrayList<>();

    public void setSubject(String subject) {
        addChangeLog("subject", this.subject, subject);
        this.subject = subject;
    }

    public void setDescription(String description) {
        addChangeLog("description", this.description, description);
        this.description = description;
    }

    public void setStatus(EventStatus status) {
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

    public void setGuest(String guest) {
        addChangeLog("guest", this.guest, guest);
        this.guest = guest;
    }

    public void setCompany(Company company) {
        addChangeLog("company", this.company != null ? this.company.getName() : null, company != null ? company.getName() : null);
        this.company = company;
    }

    @Override
    public Long getEntityId() {
        return this.id;
    }

    @Override
    public String getEntityName() {
        return getName();
    }

    @Override
    public EntityTypeEnum getEntityType() {
        return EntityTypeEnum.EVENT;
    }
}
