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
import uz.uzkassa.cts.domain.Reference;
import uz.uzkassa.cts.domain.User;
import uz.uzkassa.cts.domain.base.AbstractAuditingEntity;
import uz.uzkassa.cts.enums.ApproverCommonStatus;
import uz.uzkassa.cts.enums.EntityTypeEnum;
import uz.uzkassa.cts.enums.Priority;
import uz.uzkassa.cts.interfaces.Auditable;

import java.io.Serial;
import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "benefit_request",
        indexes = {
                @Index(columnList = "approver_id", name = "benefit_request_approver_id_idx"),
                @Index(columnList = "type_id", name = "benefit_request_type_id_idx"),
                @Index(columnList = "status", name = "benefit_request_status_idx"),
                @Index(columnList = "benefit_request_number", name = "benefit_request_number_idx"),
                @Index(columnList = "company_id", name = "benefit_request_company_id_idx")
        })
@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class BenefitRequest extends AbstractAuditingEntity implements Auditable {

    @Serial
    private static final long serialVersionUID = 654698657397L;

    @Column(name = "benefit_request_number")
    String benefitRequestNumber;

    @Column(name = "increment_number")
    Long incrementNumber;

    @Column(name = "description")
    @JdbcTypeCode(SqlTypes.LONGVARCHAR)
    String description;

    @Enumerated(EnumType.STRING)
    @Column(name = "priority")
    Priority priority = Priority.LOW;

    @Column(name = "status")
    @Enumerated(EnumType.STRING)
    ApproverCommonStatus status = ApproverCommonStatus.DRAFT;

    @NotNull
    @Column(name = "request_date", nullable = false)
    LocalDate requestDate;

    @NotNull
    @Column(name = "approver_id", nullable = false)
    Long approverId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "approver_id", updatable = false, insertable = false)
    User approver;

    @NotNull
    @Column(name = "type_id", nullable = false)
    Long typeId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "type_id", updatable = false, insertable = false)
    Reference type;

    @NotNull
    @Column(name = "quantity", nullable = false)
    BigDecimal quantity = BigDecimal.valueOf(1);

    @Column(name = "company_id")
    Long companyId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "company_id", updatable = false, insertable = false)
    Company company;

    public void setDescription(String description) {
        addChangeLog("description", this.description, description);
        this.description = description;
    }

    public void setPriority(Priority priority) {
        addChangeLog("priority", this.priority != null ? this.priority.getName() : null, priority != null ? priority.getName() : null);
        this.priority = priority;
    }

    public void setStatus(ApproverCommonStatus status) {
        addChangeLog("status", this.status != null ? this.status.getName() : null, status != null ? status.getName() : null);
        this.status = status;
    }

    public void setRequestDate(LocalDate requestDate) {
        addChangeLog("requestDate", this.requestDate != null ? String.valueOf(this.requestDate) : null, requestDate != null ? String.valueOf(requestDate) : null);
        this.requestDate = requestDate;
    }

    public void setApprover(User approver) {
        addChangeLog("approver", this.approver != null ? this.approver.getName() : null, approver != null ? approver.getName() : null);
        this.approver = approver;
    }

    public void setCompany(Company company) {
        addChangeLog("company", this.company != null ? this.company.getName() : null, company != null ? company.getName() : null);
        this.company = company;
    }

    public void setType(Reference type) {
        addChangeLog("type", this.type != null ? this.type.getName() : null, type != null ? type.getName() : null);
        this.type = type;
    }

    public void setQuantity(BigDecimal quantity) {
        addChangeLog("quantity", this.quantity != null ? this.quantity.toString() : null, quantity != null ? quantity.toString() : null);
        this.quantity = quantity;
    }

    @Override
    public Long getEntityId() {
        return this.id;
    }

    @Override
    public String getEntityName() {
        return this.getBenefitRequestNumber();
    }

    @Override
    public EntityTypeEnum getEntityType() {
        return EntityTypeEnum.BENEFIT_REQUEST;
    }
}
