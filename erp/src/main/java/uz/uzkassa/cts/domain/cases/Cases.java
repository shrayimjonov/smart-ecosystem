package uz.uzkassa.cts.domain.cases;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;
import org.hibernate.type.SqlTypes;
import uz.uzkassa.cts.domain.Customer;
import uz.uzkassa.cts.domain.User;
import uz.uzkassa.cts.domain.base.AbstractAuditingEntity;
import uz.uzkassa.cts.enums.*;
import uz.uzkassa.cts.interfaces.Auditable;

import java.io.Serial;

@Entity
@Table(name = "cases")
@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
@SQLDelete(sql = "UPDATE cases SET deleted = 'true' WHERE id=?")
@SQLRestriction(value = "deleted = 'false'")
public class Cases extends AbstractAuditingEntity implements Auditable {

    @Serial
    private static final long serialVersionUID = 5323239L;

    @Enumerated(EnumType.ORDINAL)
    @Column(name = "status")
    CaseStatus status = CaseStatus.DRAFT;

    @Enumerated(EnumType.STRING)
    @Column(name = "case_type")
    CaseType caseType;

    @Enumerated(EnumType.STRING)
    @Column(name = "priority")
    Priority priority = Priority.LOW;

    @Column(name = "customer_id")
    Long customerId;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "customer_id", updatable = false, insertable = false)
    Customer customer;

    @Column(name = "assignee_id")
    Long assigneeId;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "assignee_id", updatable = false, insertable = false)
    User assignee;

    @JdbcTypeCode(SqlTypes.LONGVARCHAR)
    @Column(name = "json_properties")
    private String jsonProperties;

    public String getNumber() {
        return PrefixEnum.CASE + "/" + this.getId();
    }

    @Override
    public String getName() {
        return getNumber();
    }

    @Override
    public Long getEntityId() {
        return this.getId();
    }

    @Override
    public EntityTypeEnum getEntityType() {
        return EntityTypeEnum.CASES;
    }
}
