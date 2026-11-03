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
import uz.uzkassa.cts.enums.ContractStatus;
import uz.uzkassa.cts.enums.DeviceType;
import uz.uzkassa.cts.enums.PrefixEnum;

import java.io.Serial;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Entity
@Table(name = "payment_contract")
@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
@SQLDelete(sql = "UPDATE payment_contract SET deleted = 'true' WHERE id=?")
@SQLRestriction(value = "deleted = 'false'")
public class PaymentContract extends AbstractAuditingEntity {

    @Serial
    private static final long serialVersionUID = 6060279893L;

    @Column(name = "payment_quote_id")
    Long paymentQuoteId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "payment_quote_id", updatable = false, insertable = false)
    PaymentQuote paymentQuote;

    @OneToMany(mappedBy = "contract", cascade = CascadeType.ALL, orphanRemoval = true)
    List<PaymentAct> paymentActs = new ArrayList<>();

    @Column(name = "customer_id", nullable = false)
    Long customerId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "customer_id", insertable = false, updatable = false)
    Customer customer;

    @Column(name = "file_uri")
    String fileUri;

    @Column(name = "file_name")
    String fileName;

    @Column(name = "status", nullable = false)
    @Enumerated(EnumType.STRING)
    ContractStatus status;

    @Column(name = "branch_id")
    Long branchId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "branch_id", insertable = false, updatable = false)
    Branch branch;

    @NotNull
    @Column(name = "company_id", nullable = false)
    Long companyId;

    @ManyToOne(cascade = CascadeType.PERSIST, fetch = FetchType.LAZY)
    @JoinColumn(name = "company_id", updatable = false, insertable = false)
    Company company;

    @Column(name = "total_product_count", columnDefinition = "integer default 1")
    Integer totalProductCount;

    @Column(name = "total_price_with_vat")
    BigDecimal totalPriceWithVat;

    @Column(name = "price_for_1_pc")
    BigDecimal priceFor1pc;

    @Column(name = "price_for_1_pc_with_vat")
    BigDecimal priceFor1pcWithVat;

    @Column(name = "vat_1pc")
    BigDecimal vat1pc;

    @Column(name = "trade_in_price_history_id")
    Long tradeInPriceHistoryId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "trade_in_price_history_id", insertable = false, updatable = false)
    TradeInPriceHistory tradeInPriceHistory;

    @Enumerated(EnumType.STRING)
    @Column(name = "device_type")
    DeviceType deviceType;

    @Column(name = "individual_contract", columnDefinition = "boolean default false")
    boolean isIndividualContract = false;

    @Column(name = "individual_contract_number")
    String individualContractNumber;

    @Column(name = "individual_date")
    LocalDateTime individualDate;

    @OneToMany(mappedBy = "paymentContract", cascade = CascadeType.ALL, orphanRemoval = true)
    Set<PaymentContractDetail> details = new HashSet<>();

    public String getNumber() {
        return this.isIndividualContract ? this.individualContractNumber : PrefixEnum.CP + "/" + this.getId();
    }

    @Override
    public LocalDateTime getCreatedDate() {
        return this.isIndividualContract ? this.individualDate : super.getCreatedDate();
    }

    public void addPaymentAct(PaymentAct paymentAct) {
        paymentAct.setContract(this);
        paymentActs.add(paymentAct);
    }
}
