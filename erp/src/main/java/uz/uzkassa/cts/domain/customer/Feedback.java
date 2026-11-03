package uz.uzkassa.cts.domain.customer;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.type.SqlTypes;
import uz.uzkassa.cts.domain.CustomerBranch;
import uz.uzkassa.cts.domain.Reference;
import uz.uzkassa.cts.domain.base.AbstractAuditingEntity;
import uz.uzkassa.cts.enums.FeedBackStatus;

import java.io.Serial;

@Getter
@Setter
@Table(name = "feedback",
        indexes = {
                @Index(columnList = "type_id", name = "feedback_type_id_idx"),
                @Index(columnList = "customer_branch_id", name = "feedback_customer_branch_id_idx"),
                @Index(columnList = "status", name = "feedback_customer_status_idx")
        })
@Entity
@SQLDelete(sql = "UPDATE feedback SET deleted = 'true' WHERE id=?")
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Feedback extends AbstractAuditingEntity {

    @Serial
    private static final long serialVersionUID = 9778932345L;

    @Column(name = "rate")
    Integer rate;

    @Column(name = "status")
    @Enumerated(EnumType.STRING)
    FeedBackStatus status = FeedBackStatus.NEW;

    @NotNull
    @JdbcTypeCode(SqlTypes.LONGNVARCHAR)
    @Column(name = "description", nullable = false)
    String description;

    @NotNull
    @Column(name = "type_id", nullable = false)
    Long typeId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "type_id", updatable = false, insertable = false)
    Reference type;

    @NotNull
    @Column(name = "customer_branch_id", nullable = false)
    Long customerBranchId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "customer_branch_id", updatable = false, insertable = false)
    CustomerBranch customerBranch;
}
