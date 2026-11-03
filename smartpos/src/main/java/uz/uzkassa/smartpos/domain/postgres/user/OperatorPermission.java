package uz.uzkassa.smartpos.domain.postgres.user;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;
import uz.uzkassa.smartpos.domain.postgres.base.audit.AbstractAuditingEntity;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@Table(name = "operator_permission", indexes = {
    @Index(columnList = "tin", name = "tin_idx"),
    @Index(columnList = "user_id", name = "user_id_idx"),
    @Index(columnList = "user_id ,tin", name = "user_id_tin_idx")
})
@SQLDelete(sql = "UPDATE operator_permission SET deleted = 'true' WHERE id=?")
@SQLRestriction("deleted = 'false'")
public class OperatorPermission extends AbstractAuditingEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "deleted", columnDefinition = "boolean default false")
    private boolean deleted = false;

    private String code;

    private String tin;

    private Long clientId;

    private Long companyId;

    private boolean activated = Boolean.FALSE;

    @Column(name = "user_id")
    private Long userId;
    @ManyToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", updatable = false, insertable = false, foreignKey = @ForeignKey(ConstraintMode.NO_CONSTRAINT))
    private User user;

    @Column(name = "expire_date")
    private LocalDateTime expireDate;

    public OperatorPermission() {

    }
}
