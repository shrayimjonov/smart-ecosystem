package uz.uzkassa.cts.domain;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;
import org.hibernate.type.SqlTypes;
import uz.uzkassa.cts.domain.address.BaseAddress;
import uz.uzkassa.cts.domain.base.AbstractAuditingEntity;
import uz.uzkassa.cts.enums.PaymentQuoteStatus;
import uz.uzkassa.cts.enums.PaymentQuoteType;

import java.io.Serial;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "payment_quote")
@Getter
@Setter
@SQLDelete(sql = "UPDATE payment_quote SET deleted = 'true' WHERE id=?")
@SQLRestriction(value = "deleted = 'false'")
public class PaymentQuote extends AbstractAuditingEntity {

    @Serial
    private static final long serialVersionUID = 8774637992L;

    @NotEmpty
    @Column(name = "customer_name", nullable = false)
    private String customerName;

    @NotEmpty
    @Size(min = 9, max = 14)
    @Column(name = "tin", nullable = false)
    private String tin;

    @Column(name = "qty", nullable = false)
    private Integer qty = 1;

    @NotEmpty
    @Column(name = "director", nullable = false)
    private String director;

    @Column(name = "phone")
    private String phone;

    @JdbcTypeCode(SqlTypes.LONGNVARCHAR)
    @Column(name = "description")
    private String description;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 50, columnDefinition = "varchar(50) default 'PENDING'")
    private PaymentQuoteStatus status = PaymentQuoteStatus.PENDING;

    @Enumerated(EnumType.STRING)
    @Column(name = "payment_status")
    private uz.uzkassa.cts.enums.PaymentQuote paymentStatus = uz.uzkassa.cts.enums.PaymentQuote.PENDING;

    @Enumerated(EnumType.STRING)
    @Column(name = "payment_type")
    private PaymentQuoteType paymentType;

    @Embedded
    private BaseAddress address;

    @Column(name = "model_id", nullable = false)
    private Long modelId;

    @ManyToOne(cascade = CascadeType.PERSIST, fetch = FetchType.LAZY)
    @JoinColumn(name = "model_id", updatable = false, insertable = false)
    private DeviceModel model;

    @OneToMany(mappedBy = "paymentQuote", cascade = CascadeType.PERSIST, orphanRemoval = true)
    private Set<PaymentContract> paymentContracts = new HashSet<>();
}
