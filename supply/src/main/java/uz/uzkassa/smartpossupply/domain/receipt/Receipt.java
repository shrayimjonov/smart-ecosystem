package uz.uzkassa.smartpossupply.domain.receipt;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;
import uz.uzkassa.smartpossupply.domain.Branch;
import uz.uzkassa.smartpossupply.domain.base.AbstractAuditingEntity;
import uz.uzkassa.smartpossupply.domain.company.Company;
import uz.uzkassa.smartpossupply.domain.stock.Stock;
import uz.uzkassa.smartpossupply.dto.receipt.ReceiptLinkDTO;
import uz.uzkassa.smartpossupply.dto.shift.XReportDTO;
import uz.uzkassa.smartpossupply.dto.shift.ZReportStatisticsDTO;
import uz.uzkassa.smartpossupply.enums.EntityTypeEnum;
import uz.uzkassa.smartpossupply.enums.ReceiptStatus;
import uz.uzkassa.smartpossupply.enums.Source;
import uz.uzkassa.smartpossupply.interfaces.Auditable;

import java.io.Serial;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Powered by: Shuxratjon Rayimjonov
 * Date: 19.12.2022
 */
@SqlResultSetMappings({

        @SqlResultSetMapping(
                name = "X-ReportMapper",
                classes = @ConstructorResult(
                        targetClass = XReportDTO.class,
                        columns = {
                                @ColumnResult(name = "receiptCount", type = Long.class),
                                @ColumnResult(name = "returnedReceiptCount", type = Long.class),
                                @ColumnResult(name = "totalCard", type = BigDecimal.class),
                                @ColumnResult(name = "totalReturnedCard", type = BigDecimal.class),
                                @ColumnResult(name = "totalVat", type = BigDecimal.class),
                                @ColumnResult(name = "totalReturnedVat", type = BigDecimal.class),
                                @ColumnResult(name = "totalCash", type = BigDecimal.class),
                                @ColumnResult(name = "totalReturnedCash", type = BigDecimal.class),
                        }
                )
        ),

        @SqlResultSetMapping(
                name = "Z-ReportStatisticMapper",
                classes = @ConstructorResult(
                        targetClass = ZReportStatisticsDTO.class,
                        columns = {
                                @ColumnResult(name = "totalCard", type = BigDecimal.class),
                                @ColumnResult(name = "totalCash", type = BigDecimal.class)
                        }
                )
        )

}
)
@Entity
@Table(name = "receipt", indexes = {
        @Index(columnList = "uid", name = "receipt_uid_idx"),
        @Index(columnList = "terminal_serial_number", name = "receipt_terminal_serial_number_idx"),
        @Index(columnList = "shift_number", name = "receipt_shift_number_idx"),
        @Index(columnList = "company_id", name = "receipt_company_id_idx"),
        @Index(columnList = "customer_id", name = "receipt_customer_id_idx"),
})
@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Receipt extends AbstractAuditingEntity implements Auditable {

    @Serial
    private static final long serialVersionUID = 8984342343L;

    @Column(name = "uid", unique = true)
    String uid;

    @Column(name = "fiscal_sign", length = 50)
    String fiscalSign;

    @JdbcTypeCode(SqlTypes.LONGVARCHAR)
    @Column(name = "fiscal_url")
    String fiscalUrl;

    @Column(name = "terminal_serial_number")
    String terminalSerialNumber;

    @Column(name = "terminal_fiscal_number")
    String terminalFiscalNumber;

    @Column(name = "receipt_seq")
    String receiptSeq;

    @Column(name = "shift_number")
    Long shiftNumber;

    @Column(name = "position_count")
    Integer positionCount;

    @Column(name = "total_qty", precision = 25, scale = 2)
    BigDecimal totalQty = BigDecimal.ZERO;

    @Column(name = "total_cash", precision = 25, scale = 2)
    BigDecimal totalCash;

    @Column(name = "total_card", precision = 25, scale = 2)
    BigDecimal totalCard;

    @Column(name = "total_price", precision = 25, scale = 2)
    BigDecimal totalPrice;

    @Column(name = "total_paid", precision = 25, scale = 2)
    BigDecimal totalPaid;

    @Column(name = "total_discount", precision = 25, scale = 2)
    BigDecimal totalDiscount;

    @Column(name = "discount_percent", precision = 25, scale = 2)
    BigDecimal discountPercent;

    @Column(name = "total_vat", precision = 25, scale = 2)
    BigDecimal totalVat = BigDecimal.ZERO;

    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    ReceiptStatus status = ReceiptStatus.DRAFT;

    @OneToMany(mappedBy = "receipt", cascade = CascadeType.ALL, orphanRemoval = true)
    @OrderBy("id")
    List<ReceiptPayment> payments = new ArrayList<>();

    @Column(name = "branch_id")
    Long branchId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "branch_id", updatable = false, insertable = false)
    Branch branch;

    @Column(name = "company_id")
    Long companyId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "company_id", updatable = false, insertable = false)
    Company company;

    @Column(name = "customer_id")
    Long customerId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "customer_id", updatable = false, insertable = false)
    Company customer;

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "receipt", orphanRemoval = true)
    @OrderBy("id")
    Set<ReceiptItem> items = new HashSet<>();

    @Enumerated(EnumType.STRING)
    Source source;

    @Column(name = "stock_out_id")
    Long stockOutId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "stock_out_id", updatable = false, insertable = false)
    Stock stockOut;

    @Column(name = "stock_return_id")
    Long stockReturnId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "stock_return_id", updatable = false, insertable = false)
    Stock stockReturn;

    @Column(name = "app_version")
    String appVersion;

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

    public ReceiptLinkDTO toLinkDTO() {
        return new ReceiptLinkDTO(this.getId(), this.getName(), this.getStatus());
    }

    @Override
    public Long getEntityId() {
        return this.id;
    }

    @Override
    public String getName() {
        return getUid();
    }

    @Override
    public EntityTypeEnum getEntityType() {
        return EntityTypeEnum.RECEIPT;
    }

    @Override
    public String getEntityName() {
        return getName();
    }
}
