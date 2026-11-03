package uz.uzkassa.cts.domain;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;
import uz.uzkassa.cts.domain.base.AbstractAuditingEntity;
import uz.uzkassa.cts.enums.ContractStatus;
import uz.uzkassa.cts.enums.PrefixEnum;
import uz.uzkassa.cts.utils.NumberToWordUtil;

import java.io.Serial;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@Getter
@Setter
@Table(name = "payment_schedule_invoice")
@Entity(name = "payment_schedule_invoice")
@SQLDelete(sql = "UPDATE payment_schedule_invoice SET deleted = 'true' WHERE id=?")
@SQLRestriction(value = "deleted = 'false'")
public class PaymentScheduleInvoice extends AbstractAuditingEntity {

    @Serial
    private static final long serialVersionUID = 1020562235L;

    @Column(name = "status")
    @Enumerated(EnumType.STRING)
    private ContractStatus status;

    @Column(name = "total_product_count")
    private Integer totalProductCount;

    @Column(name = "service_name")
    private String serviceName;

    @Column(name = "total_sell_price")
    private BigDecimal totalSellPrice;

    @Column(name = "total_price")
    private BigDecimal totalPrice;

    @Column(name = "total_vat_price")
    private BigDecimal totalVatPrice;

    @OneToOne(mappedBy = "invoice", cascade = CascadeType.ALL, orphanRemoval = true)
    protected ActPaymentSchedule schedule;

    @OneToMany(mappedBy = "invoice", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<PaymentScheduleInvoiceDetail> details = new HashSet<>();

    @NotNull
    @Column(name = "contract_id")
    private Long contractId;

    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinColumn(name = "contract_id", insertable = false, updatable = false)
    private ContractForLeasing contract;

    public String getNumber() {
        return PrefixEnum.CLII + "/" + this.getId();
    }

    public String decimalToWord(BigDecimal amount) {
        return NumberToWordUtil.digits2Text(amount);
    }

    public void addDetail(PaymentScheduleInvoiceDetail detail) {
        detail.setInvoice(this);
        details.add(detail);
    }

    public String createDescription(String actNumber, LocalDate actCreateDate,
                                    ContractForLeasing con, BigDecimal amount) {
        return " Договор № " +
                con.getNumber() +
                " от " + con.getCreatedDate().toLocalDate() + " г." +
                " Акт  №  " +
                actNumber +
                " от " + actCreateDate + " г." +
                " Общ.сумма " + amount;
    }
}
