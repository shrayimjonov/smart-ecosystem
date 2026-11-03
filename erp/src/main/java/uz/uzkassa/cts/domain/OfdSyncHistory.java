package uz.uzkassa.cts.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;
import uz.uzkassa.cts.domain.base.AbstractAuditingEntity;
import uz.uzkassa.cts.enums.OfdSyncType;

import java.io.Serial;

@Entity
@Table(name = "ofd_sync_history")
@Getter
@Setter
public class OfdSyncHistory extends AbstractAuditingEntity {

    @Serial
    private static final long serialVersionUID = 3675447820L;

    @Column(name = "customer_id")
    private Long customerId;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "customer_id", updatable = false, insertable = false)
    private Customer customer;

    @Column(name = "customer_branch_id")
    private Long customerBranchId;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "customer_branch_id", updatable = false, insertable = false)
    private CustomerBranch customerBranch;

    @JdbcTypeCode(SqlTypes.LONGNVARCHAR)
    @Column(name = "message")
    private String message;

    @Column(name = "field")
    private String field;

    @Column(name = "error_code")
    private Integer errorCode;

    @Enumerated(EnumType.STRING)
    @Column(name = "sync_type")
    private OfdSyncType syncType;


}
