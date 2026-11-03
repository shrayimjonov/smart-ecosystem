package uz.uzkassa.cts.domain;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;
import uz.uzkassa.cts.domain.base.AbstractAuditingEntity;
import uz.uzkassa.cts.enums.DeviceType;
import uz.uzkassa.cts.enums.apay.APayStatus;

import java.io.Serial;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "bill")
@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
@SQLDelete(sql = "UPDATE bill SET deleted = 'true' WHERE id=?")
@SQLRestriction(value = "deleted = 'false'")
public class Bill extends AbstractAuditingEntity {

    @Serial
    static final long serialVersionUID = 7751414622L;

    @Column(name = "bill_id")
    String billId;

    @Column(name = "pay_status")
    @Enumerated(EnumType.STRING)
    APayStatus payStatus;

    @Column(name = "fiscal_check_url")
    String fiscalCheckUrl;

    @Column(name = "terminal_id")
    String terminalId;

    @Column(name = "fiscal_sign")
    String fiscalSign;

    @Column(name = "pan")
    String pan;

    @Column(name = "expiry")
    String expiry;

    @Column(name = "amount")
    BigDecimal amount;

    @Column(name = "pay_time")
    LocalDateTime payTime;

    @Column(name = "pay_error_message")
    String payErrorMessage;

    @NotNull
    @Column(name = "payment_quote_id", nullable = false)
    Long paymentQuoteId;

    @ManyToOne(cascade = CascadeType.PERSIST, fetch = FetchType.LAZY)
    @JoinColumn(name = "payment_quote_id", updatable = false, insertable = false)
    PaymentQuote paymentQuote;

    @NotNull
    @Column(name = "branch_id")
    Long branchId;

    @ManyToOne(cascade = CascadeType.PERSIST, fetch = FetchType.LAZY)
    @JoinColumn(name = "branch_id", updatable = false, insertable = false)
    Branch branch;

    @Column(name = "device_id")
    Long deviceId;

    @ManyToOne(cascade = CascadeType.PERSIST, fetch = FetchType.LAZY)
    @JoinColumn(name = "device_id", updatable = false, insertable = false)
    Device device;

    @Column(name = "fiscal_module_id")
    Long fiscalModuleId;

    @ManyToOne(cascade = CascadeType.PERSIST, fetch = FetchType.LAZY)
    @JoinColumn(name = "fiscal_module_id", updatable = false, insertable = false)
    FiscalModule fiscalModule;

    @Column(name = "customer_id")
    Long customerId;

    @ManyToOne(cascade = CascadeType.PERSIST, fetch = FetchType.LAZY)
    @JoinColumn(name = "customer_id", updatable = false, insertable = false)
    Customer customer;

    @Column(name = "customer_branch_id")
    Long customerBranchId;

    @ManyToOne(cascade = CascadeType.PERSIST, fetch = FetchType.LAZY)
    @JoinColumn(name = "customer_branch_id", updatable = false, insertable = false)
    CustomerBranch customerBranch;

    @Column(name = "customer_branch_device_id")
    Long customerBranchDeviceId;

    @ManyToOne(cascade = CascadeType.PERSIST, fetch = FetchType.LAZY)
    @JoinColumn(name = "customer_branch_device_id", updatable = false, insertable = false)
    CustomerBranchDevice customerBranchDevice;

    @Enumerated(EnumType.STRING)
    @Column(name = "device_type")
    DeviceType deviceType;

    @Column(name = "trade_in_price_history_id")
    Long tradeInPriceHistoryId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "trade_in_price_history_id", insertable = false, updatable = false)
    TradeInPriceHistory tradeInPriceHistory;
}
