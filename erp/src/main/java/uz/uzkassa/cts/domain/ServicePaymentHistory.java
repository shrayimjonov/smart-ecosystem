package uz.uzkassa.cts.domain;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;
import uz.uzkassa.cts.domain.base.AbstractAuditingEntity;

import java.io.Serial;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.Month;

@Entity
@Table(
    name = "service_payment_history",
    uniqueConstraints = {
        @UniqueConstraint(columnNames = {"device_serial_number", "month", "customer_id", "year", "tariff_id", "due_date"})
    },
    indexes = {
        @Index(columnList = "customer_id", name = "sph_customer_id_idx"),
        @Index(columnList = "created_date", name = "sph_created_date_idx"),
        @Index(columnList = "service_order_invoice_id", name = "sph_service_order_invoice_id_idx"),
        @Index(columnList = "start_date", name = "sph_start_date_idx")
    })
@Getter
@Setter
@SQLDelete(sql = "UPDATE service_payment_history SET deleted = 'true' WHERE id=?")
@SQLRestriction(value = "deleted = 'false'")
public class ServicePaymentHistory extends AbstractAuditingEntity {

    @Serial
    private static final long serialVersionUID = 837728290L;

    @NotNull
    @Column(name = "customer_branch_device_id", nullable = false)
    private Long customerBranchDeviceId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "customer_branch_device_id", updatable = false, insertable = false)
    private CustomerBranchDevice customerBranchDevice;

    @NotNull
    @Column(name = "customer_id", nullable = false)
    private Long customerId;

    @ManyToOne(cascade = CascadeType.PERSIST, fetch = FetchType.LAZY)
    @JoinColumn(name = "customer_id", updatable = false, insertable = false, foreignKey = @ForeignKey(ConstraintMode.NO_CONSTRAINT))
    private Customer customer;

    @NotNull
    @Column(name = "contract_id", nullable = false)
    private Long contractId;

    @ManyToOne(cascade = CascadeType.PERSIST, fetch = FetchType.LAZY)
    @JoinColumn(name = "contract_id", updatable = false, insertable = false, foreignKey = @ForeignKey(ConstraintMode.NO_CONSTRAINT))
    private Contract contract;

    @Column(name = "service_order_invoice_id")
    private Long serviceOrderInvoiceId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "service_order_invoice_id", updatable = false, insertable = false, foreignKey = @ForeignKey(ConstraintMode.NO_CONSTRAINT))
    private ServiceOrderInvoice serviceOrderInvoice;

    @NotNull
    @Column(name = "tariff_id", nullable = false)
    private Long tariffId;

    @ManyToOne(cascade = CascadeType.PERSIST, fetch = FetchType.LAZY)
    @JoinColumn(name = "tariff_id", updatable = false, insertable = false, foreignKey = @ForeignKey(ConstraintMode.NO_CONSTRAINT))
    private Tariff tariff;

    @Column(name = "device_serial_number")
    private String deviceSerialNumber;

    @Column(name = "month", nullable = false)
    private Month month;

    @Column(name = "year", nullable = false)
    private Integer year;

    @Column(name = "due_date", nullable = false)
    private LocalDate dueDate;

    @Column(name = "start_date", nullable = false)
    private LocalDate startDate;

    @Column(name = "amount", precision = 19, scale = 3)
    private BigDecimal amount;

    @Column(name = "paid", columnDefinition = "boolean default false")
    private boolean paid = false;


    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;

        ServicePaymentHistory that = (ServicePaymentHistory) o;

        if (!customerId.equals(that.customerId))
            return false;
        if (!deviceSerialNumber.equals(that.deviceSerialNumber))
            return false;
        if (month != that.month)
            return false;
        if (!year.equals(that.year))
            return false;
        if (!tariffId.equals(that.tariffId))
            return false;
        return dueDate != that.dueDate;
    }

    @Override
    public int hashCode() {
        int result = customerId.hashCode();
        result = 31 * result + deviceSerialNumber.hashCode();
        result = 31 * result + month.hashCode();
        result = 31 * result + year.hashCode();
        result = 31 * result + tariffId.hashCode();
        result = 31 * result + dueDate.hashCode();
        return result;
    }
}
