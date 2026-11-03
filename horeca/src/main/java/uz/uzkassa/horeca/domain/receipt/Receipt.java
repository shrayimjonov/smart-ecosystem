package uz.uzkassa.horeca.domain.receipt;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import org.apache.commons.lang3.StringUtils;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;
import org.hibernate.type.SqlTypes;
import uz.uzkassa.horeca.domain.Terminal;
import uz.uzkassa.horeca.domain.base.AbstractAuditingEntity;
import uz.uzkassa.horeca.domain.branch.Branch;
import uz.uzkassa.horeca.domain.company.Company;
import uz.uzkassa.horeca.domain.order.Order;
import uz.uzkassa.horeca.dto.receipt.cabinet.ReceiptCommonDTO;
import uz.uzkassa.horeca.enums.EntityTypeEnum;
import uz.uzkassa.horeca.enums.ReceiptStatus;

import java.io.Serial;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "receipt")
@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
@SQLDelete(sql = "UPDATE receipt SET deleted='true' WHERE id=?")
public class Receipt extends AbstractAuditingEntity implements Serializable {

    @Serial
    private static final long serialVersionUID = 453452642235L;

    @NotNull
    @Column(name = "uid", length = 50, unique = true, nullable = false)
    String uid;

    @Column(name = "fiscal_url", length = 1000)
    String fiscalUrl;

    @Column(name = "shift_number")
    Long shiftNumber;

    @Column(name = "number_in_shift")
    Integer numberInShift;

    @Column(name = "receipt_seq", length = 1000)
    String receiptSeq;

    @JdbcTypeCode(SqlTypes.LONGVARCHAR)
    @Column(name = "fiscal_sign")
    String fiscalSign;

    @Column(name = "receipt_date")
    LocalDateTime receiptDate;

    @Column(name = "total_price", scale = 4, precision = 25)
    BigDecimal totalPrice;

    @Column(name = "total_vat", scale = 4, precision = 25)
    BigDecimal totalVat;

    @Column(name = "total_cash", scale = 4, precision = 25)
    BigDecimal totalCash;

    @Column(name = "total_card", scale = 4, precision = 25)
    BigDecimal totalCard;

    @Column(name = "total_cost", scale = 4, precision = 25)
    BigDecimal totalCost;

    @Column(name = "total_paid", scale = 4, precision = 25)
    BigDecimal totalPaid;

    @Column(name = "discount_percent")
    BigDecimal discountPercent;

    @Column(name = "total_discount")
    BigDecimal totalDiscount;

    @Column(name = "total_excise", precision = 25, scale = 4)
    BigDecimal totalExcise;

    @Column(name = "items_count", precision = 25)
    BigDecimal itemsCount;

    @Column(name = "read_only", columnDefinition = "boolean default false")
    boolean readOnly = false;

    @Column(name = "force_to_print", columnDefinition = "boolean default false")
    boolean forceToPrint = false;

    @Column(name = "status")
    @Enumerated(EnumType.STRING)
    ReceiptStatus status = ReceiptStatus.DRAFT;

    @Column(name = "terminal_id")
    Long terminalId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "terminal_id", updatable = false, insertable = false)
    Terminal terminal;

    @Column(name = "order_id")
    Long orderId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id", updatable = false, insertable = false)
    Order order;

    @OneToMany(mappedBy = "receipt", cascade = CascadeType.ALL, orphanRemoval = true)
    @OrderBy("id")
    List<ReceiptPayment> payments = new ArrayList<>();

    @Column(name = "branch_id", nullable = false)
    Long branchId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "branch_id", updatable = false, insertable = false)
    Branch branch;

    @Column(name = "company_id", nullable = false)
    Long companyId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "company_id", updatable = false, insertable = false)
    Company company;

    @Column(name = "bill_id")
    String billId;

    @Column(name = "factura_id", unique = true)
    String facturaId;

    @OneToMany(mappedBy = "receipt", cascade = CascadeType.ALL, orphanRemoval = true)
    @SQLRestriction("deleted='false'")
    @OrderBy("id")
    List<ReceiptItem> items = new ArrayList<>();

    public void addItem(ReceiptItem receiptItem) {
        receiptItem.setReceipt(this);
        receiptItem.setReceiptId(this.id);
        items.add(receiptItem);
    }

    public void addPayment(ReceiptPayment receiptPayment) {
        receiptPayment.setReceipt(this);
        receiptPayment.setReceiptId(this.id);
        payments.add(receiptPayment);
    }

    public boolean isFiscal() {
        return StringUtils.isNoneEmpty(getFiscalSign(), getFiscalUrl());
    }

    public ReceiptCommonDTO toCommonDTO() {
        return new ReceiptCommonDTO(getId(), getUid(), getStatus());
    }

    @Override
    public Long getEntityId() {
        return id;
    }

    @Override
    public EntityTypeEnum getEntityType() {
        return EntityTypeEnum.RECEIPT;
    }
}
