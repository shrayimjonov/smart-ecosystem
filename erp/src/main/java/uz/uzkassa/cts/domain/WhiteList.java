package uz.uzkassa.cts.domain;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;
import org.hibernate.type.SqlTypes;
import uz.uzkassa.cts.domain.base.AbstractAuditingEntity;
import uz.uzkassa.cts.enums.CommonStatus;
import uz.uzkassa.cts.enums.WhiteListAction;

import java.io.Serial;
import java.time.LocalDate;

@Entity
@Table(name = "white_list",
        indexes = {
                @Index(columnList = "customer_id", name = "white_list_customer_id_idx"),
                @Index(columnList = "action", name = "white_list_action_idx"),
                @Index(columnList = "customer_id, action", name = "white_list_customer_id_action_idx")
        })
@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
@SQLDelete(sql = "UPDATE white_list SET deleted = 'true' WHERE id=?")
@SQLRestriction(value = "deleted = 'false'")
public class WhiteList extends AbstractAuditingEntity {

    @Serial
    private static final long serialVersionUID = 579845021813L;

    @Column(name = "customer_id", nullable = false)
    Long customerId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "customer_id", updatable = false, insertable = false)
    Customer customer;

    @Column(name = "from_date")
    LocalDate fromDate;

    @Column(name = "to_date")
    LocalDate toDate;

    @Column(name = "action", nullable = false)
    @Enumerated(EnumType.STRING)
    WhiteListAction action;

    @Column(name = "status")
    @Enumerated(EnumType.STRING)
    CommonStatus status;

    @JdbcTypeCode(SqlTypes.LONGVARCHAR)
    @Column(name = "description")
    String description;
}
