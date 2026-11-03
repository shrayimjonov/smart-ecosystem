package uz.uzkassa.cts.domain;

import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import uz.uzkassa.cts.domain.base.AbstractAuditingEntity;

import java.io.Serial;
import java.math.BigDecimal;

@Getter
@Setter
@Table(name = "payment_schedule_invoice_detail")
@Entity(name = "payment_schedule_invoice_detail")
@SQLDelete(sql = "UPDATE payment_schedule_invoice_detail SET deleted = 'true' WHERE id=?")
@SQLRestriction(value = "deleted = 'false'")
public class PaymentScheduleInvoiceDetail extends AbstractAuditingEntity {

    @Serial
    private static final long serialVersionUID = 2280225363L;

    @NotNull
    @Column(name = "device_id", nullable = false)
    Long deviceId;

    @ManyToOne(cascade = CascadeType.PERSIST, fetch = FetchType.LAZY)
    @JoinColumn(name = "device_id", updatable = false, insertable = false)
    Device device;

    @Column(name = "price_for_1_pc")
    private BigDecimal priceFor1pc;

    @Column(name = "vat_for_1_pc")
    private BigDecimal vatFor1pc;

    @Column(name = "price_vith_vat_for_1_pc")
    private BigDecimal priceWithVatFor1pc;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "invoice_id")
    private PaymentScheduleInvoice invoice;
}
