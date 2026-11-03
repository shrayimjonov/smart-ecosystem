package uz.uzkassa.cts.domain;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;
import uz.uzkassa.cts.domain.base.AbstractAuditingEntity;
import uz.uzkassa.cts.enums.ContractStatus;
import uz.uzkassa.cts.enums.EntityTypeEnum;
import uz.uzkassa.cts.enums.apay.APayStatus;
import uz.uzkassa.cts.interfaces.Auditable;

import java.io.Serial;
import java.math.BigDecimal;
import java.time.LocalDate;

@Getter
@Setter
@Table(name = "act_payment_schedule")
@Entity
@FieldDefaults(level = AccessLevel.PRIVATE)
@SQLDelete(sql = "UPDATE act_payment_schedule SET deleted = 'true' WHERE id=?")
@SQLRestriction(value = "deleted = 'false'")
public class ActPaymentSchedule extends AbstractAuditingEntity implements Auditable {

    @Serial
    private static final long serialVersionUID = 703104558L;

    @Column(name = "payment_date")
    LocalDate paymentDate;

    BigDecimal amount;

    BigDecimal balance;

    @Column(name = "total_product_count")
    Integer totalProductCount;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "act_detail_id")
    ActOfTransmissionDetail actOfTransmissionDetail;

    @Column(name = "invoice_id")
    Long invoiceId;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "invoice_id", insertable = false, updatable = false)
    PaymentScheduleInvoice invoice;

    @Column(name = "status")
    @Enumerated(EnumType.STRING)
    ContractStatus status;

    @Column(name = "payment_status")
    @Enumerated(EnumType.STRING)
    APayStatus paymentStatus = APayStatus.PENDING;

    @Override
    public Long getEntityId() {
        return id;
    }

    @Override
    public EntityTypeEnum getEntityType() {
        return EntityTypeEnum.ACT_PAYMENT_SCHEDULE;
    }
}
