package uz.uzkassa.horeca.domain;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;
import uz.uzkassa.horeca.domain.base.AbstractAuditingEntity;
import uz.uzkassa.horeca.domain.order.Order;
import uz.uzkassa.horeca.enums.EntityTypeEnum;
import uz.uzkassa.horeca.enums.terminal.BillStatus;

import java.io.Serializable;
import java.math.BigDecimal;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
@Entity
@Table(name = "bill")
public class Bill extends AbstractAuditingEntity implements Serializable {

    @Column(name = "amount")
    BigDecimal amount;

    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    BillStatus status = BillStatus.PENDING;

    @JdbcTypeCode(SqlTypes.LONGVARCHAR)
    @Column(name = "fiscal_url")
    String fiscalUrl;

    @JdbcTypeCode(SqlTypes.LONGVARCHAR)
    @Column(name = "eps_qr_code")
    String epsQrCode;

    @NotNull
    @Column(name = "bill_id", nullable = false)
    String billId;

    @Column(name = "terminal_id")
    Long terminalId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "terminal_id", updatable = false, insertable = false)
    Terminal terminal;

    @NotNull
    @Column(name = "order_id", nullable = false)
    Long orderId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id", updatable = false, insertable = false)
    Order order;

    @Override
    public Long getEntityId() {
        return id;
    }

    @Override
    public EntityTypeEnum getEntityType() {
        return EntityTypeEnum.BILL;
    }
}
