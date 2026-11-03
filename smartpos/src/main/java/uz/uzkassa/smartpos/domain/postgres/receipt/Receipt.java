package uz.uzkassa.smartpos.domain.postgres.receipt;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.persistence.CascadeType;
import jakarta.persistence.ForeignKey;
import jakarta.persistence.Index;
import jakarta.persistence.OrderBy;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.hibernate.annotations.*;
import org.hibernate.annotations.Parameter;
import org.hibernate.id.enhanced.SequenceStyleGenerator;
import org.hibernate.type.SqlTypes;
import uz.uzkassa.smartpos.domain.postgres.company.Branch;
import uz.uzkassa.smartpos.domain.postgres.company.Company;
import uz.uzkassa.smartpos.domain.postgres.terminal.Device;
import uz.uzkassa.smartpos.domain.postgres.user.User;
import uz.uzkassa.smartpos.dto.receipt.ReceiptDTO;
import uz.uzkassa.smartpos.dto.receipt.ReceiptDetailDTO;
import uz.uzkassa.smartpos.enums.PaymentType;
import uz.uzkassa.smartpos.enums.ReceiptStatus;
import uz.uzkassa.smartpos.enums.Source;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Entity
@Table(name = "receipt",
        indexes = {
                @Index(columnList = "branch_id,terminal_id,status", name = "rec_br_ter_sta_idx"),
                @Index(columnList = "company_id,branch_id,status", name = "rec_com_br_sta_idx"),
                @Index(columnList = "branch_id,status", name = "rec_br_sta_idx"),
                @Index(columnList = "branch_id,status,source", name = "rec_br_sta_sour_idx")
        }
)
@Getter
@Setter
@DynamicUpdate
public class Receipt implements Cloneable {
    @Id
    @GenericGenerator(
            name = "sequenceGenerator",
            type = SequenceStyleGenerator.class,
            parameters = {
                    @Parameter(
                            name = "optimizer",
                            value = "pooled-lo"
                    ),
                    @Parameter(
                            name = "initial_value",
                            value = "1"
                    ),
                    @Parameter(
                            name = "increment_size",
                            value = "1"
                    )
            }
    )
    @GeneratedValue(
            strategy = GenerationType.SEQUENCE,
            generator = "sequenceGenerator"
    )
    private Long id;

    @NotNull
    @Column(length = 50, unique = true, nullable = false)
    private String uid;
    @Column(length = 50, name = "origin_uid")
    private String originUid;

    @Column(name = "fiscal_sign", length = 50)
    private String fiscalSign;
    @Column(name = "fiscal_url", columnDefinition = "text")
    private String fiscalUrl;
    @Column(name = "shift_number")
    private Long shiftNumber;

    private Double lat;
    private Double lon;

    @Column(name = "receipt_date_time")
    private LocalDateTime receiptDateTime;

    @Column(name = "total_cash", precision = 25, scale = 4)
    private BigDecimal totalCash;

    @Column(name = "total_card", precision = 25, scale = 4)
    private BigDecimal totalCard;

    @Column(name = "total_cost", precision = 25, scale = 4)
    private BigDecimal totalCost;

    @Column(name = "total_paid", precision = 25, scale = 4)
    private BigDecimal totalPaid;

    @Column(name = "total_discount")
    private BigDecimal totalDiscount;

    @Column(name = "total_excise", precision = 25, scale = 4)
    private BigDecimal totalExcise;

    @Column(name = "discount_percent")
    private BigDecimal discountPercent;

    @Column(name = "total_nds", precision = 25, scale = 4)
    private BigDecimal totalNds;

    @Column(name = "customer_name")
    private String customerName;
    @Column(name = "customer_phone")
    private String customerPhone;
    @Column(name = "read_only", columnDefinition = "boolean default false")
    private boolean readOnly = false;
    @Column(name = "force_to_print", columnDefinition = "boolean default false")
    private boolean forceToPrint = false;

    @Enumerated(EnumType.STRING)
    @Column(length = 50)
    private ReceiptStatus status;

    @Enumerated(EnumType.STRING)
    @Column(length = 50, updatable = false)
    private Source source/* = Source.TERMINAL*/;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "receipt_payments",
            joinColumns = {@JoinColumn(name = "receipt_id", referencedColumnName = "id")})
    @MapKeyEnumerated(EnumType.STRING)
    @Column(name = "amount")
    private Map<PaymentType, BigDecimal> payments;

    @NotNull
    @Column(name = "branch_id", nullable = false)
    private Long branchId;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "branch_id", updatable = false, insertable = false, foreignKey = @ForeignKey(ConstraintMode.NO_CONSTRAINT))
    @JsonIgnore
    private Branch branch;

    @NotNull
    @Column(name = "company_id", nullable = false)
    private Long companyId;
    @ManyToOne(fetch = FetchType.LAZY)
    @NotFound(action = NotFoundAction.IGNORE)
    @JoinColumn(name = "company_id", updatable = false, insertable = false, foreignKey = @ForeignKey(ConstraintMode.NO_CONSTRAINT))
    @JsonIgnore
    private Company company;

    @Column(name = "created_by_id")
    private Long createdById;
    @ManyToOne(fetch = FetchType.LAZY)
    @NotFound(action = NotFoundAction.IGNORE)
    @JoinColumn(name = "created_by_id", updatable = false, insertable = false, foreignKey = @ForeignKey(ConstraintMode.NO_CONSTRAINT))
    @JsonIgnore
    private User createdBy;

    @Column(name = "terminal_id")
    private Long terminalId;
    @ManyToOne(fetch = FetchType.LAZY)
    @NotFound(action = NotFoundAction.IGNORE)
    @JoinColumn(name = "terminal_id", updatable = false, insertable = false, foreignKey = @ForeignKey(ConstraintMode.NO_CONSTRAINT))
    @JsonIgnore
    private Device terminal;

    @Column(name = "created_date_time")
    private LocalDateTime createdDateTime;

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "receipt", orphanRemoval = true)
    @JsonIgnore
    @OrderBy("productName")
    private Set<ReceiptDetail> receiptDetails = new HashSet<>();

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(columnDefinition = "jsonb")
    private Additional additional;

    @Column(name = "bill_id")
    private String billId;
    @Column(name = "factura_id")
    private String facturaId;
    @Column(name = "send_sms", columnDefinition = "boolean default false")
    private boolean sendSms;

    public Set<ReceiptDetail> getReceiptDetails() {
        if (receiptDetails == null) {
            receiptDetails = new HashSet<>();
        }
        return receiptDetails;
    }

    public ReceiptDTO toListDto() {
        ReceiptDTO dto = new ReceiptDTO();
        dto.setId(getId());
        dto.setUid(getUid());
        dto.setFiscalSign(getFiscalSign());
        dto.setFiscalUrl(getFiscalUrl());
        dto.setReceiptDateTime(getReceiptDateTime());
        dto.setTotalCost(getTotalCost());
        dto.setTotalPaid(getTotalPaid());
        dto.setTotalCash(getTotalCash());
        dto.setTotalCard(getTotalCard());
        dto.setTotalNds(getTotalNds());
        dto.setStatus(getStatus());
        dto.setShiftNo(getShiftNumber());
        dto.setBranchId(getBranchId());
        dto.setTerminalId(getTerminalId());
        dto.setCustomerName(getCustomerName());
        dto.setCustomerPhone(getCustomerPhone());
        dto.setSource(getSource());
        if (getTerminal() != null) {
            dto.setTerminalSN(getTerminal().getSerialNumber());
        }
        dto.setPayments(getPayments());
        dto.setSendSms(isSendSms());
        dto.setFacturaId(getFacturaId());
        return dto;
    }

    public ReceiptDTO getDTO(boolean withMarks, boolean noFiscal) {
        ReceiptDTO receiptDTO = new ReceiptDTO();
        receiptDTO.setId(getId());
        receiptDTO.setLat(getLat());
        receiptDTO.setLon(getLon());
        receiptDTO.setOriginUid(getOriginUid());
        if (!noFiscal) {
            receiptDTO.setUid(getUid());
            receiptDTO.setFiscalSign(getFiscalSign());
            receiptDTO.setFiscalUrl(getFiscalUrl());
        }
        receiptDTO.setCreatedDateTime(getCreatedDateTime());
        receiptDTO.setReceiptDateTime(getReceiptDateTime());
        receiptDTO.setTotalCash(getTotalCash());
        receiptDTO.setTotalCard(getTotalCard());
        receiptDTO.setTotalCost(getTotalCost());
        receiptDTO.setTotalPaid(getTotalPaid());
        receiptDTO.setTotalDiscount(getTotalDiscount());
        receiptDTO.setTotalExcise(getTotalExcise());
        receiptDTO.setDiscountPercent(getDiscountPercent());
        receiptDTO.setCustomerName(getCustomerName());
        receiptDTO.setCustomerPhone(getCustomerPhone());
        receiptDTO.setReadOnly(isReadOnly());
        receiptDTO.setForceToPrint(isForceToPrint());
        receiptDTO.setTerminalId(getTerminalId());

        if (getTerminal() != null) {
            receiptDTO.setTerminalSN(getTerminal().getSerialNumber());
        }
        receiptDTO.setBranchId(getBranchId());
        if (getBranch() != null) {
            receiptDTO.setBranchName(getBranch().getName());
            receiptDTO.setBranchAddress(getBranch().getAddress());
        }
        receiptDTO.setCompanyId(getCompanyId());
        if (getCompany() != null) {
            receiptDTO.setCompanyName(getCompany().getName());
            receiptDTO.setCompanyINN(getCompany().getInn());
        }
        receiptDTO.setUserId(getCreatedById());
        if (getCreatedBy() != null) {
            receiptDTO.setUser(getCreatedBy().getShortDTO());
        }
        if (CollectionUtils.isNotEmpty(getReceiptDetails())) {
            LinkedList<ReceiptDetailDTO> details = new LinkedList<>();
            for (ReceiptDetail detail : getReceiptDetails()) {
                details.add(detail.toDTO(withMarks));
            }
            receiptDTO.setReceiptDetails(details);
        }
        receiptDTO.setStatus(getStatus());
        receiptDTO.setShiftNo(getShiftNumber());
        receiptDTO.setTotalNds(getTotalNds());
        if (getCreatedBy() != null) {
            receiptDTO.setUser(getCreatedBy().getShortDTO());
        }
        if (getPayments() != null) {
            receiptDTO.setPayments(getPayments());
        }

        receiptDTO.setAdditional(getAdditional());
        receiptDTO.setBillId(getBillId());
        receiptDTO.setFacturaId(getFacturaId());
        receiptDTO.setSendSms(isSendSms());
        return receiptDTO;
    }

    public void addChild(ReceiptDetail detail) {
        detail.setReceipt(this);
        receiptDetails.add(detail);
    }

    public boolean isFiscal() {
        return StringUtils.isNoneEmpty(getFiscalSign(), getFiscalUrl());
    }

    @Override
    public Receipt clone() {
        Receipt clone = new Receipt();
        clone.setUid(getUid());
        clone.setOriginUid(getOriginUid());
        clone.setShiftNumber(getShiftNumber());
        clone.setLat(getLat());
        clone.setLon(getLon());
        clone.setReceiptDateTime(getReceiptDateTime());
        clone.setTotalCash(getTotalCash());
        clone.setTotalCard(getTotalCard());
        clone.setTotalCost(getTotalCost());
        clone.setTotalPaid(getTotalPaid());
        clone.setTotalDiscount(getTotalDiscount());
        clone.setTotalExcise(getTotalExcise());
        clone.setDiscountPercent(getDiscountPercent());
        clone.setTotalNds(getTotalNds());
        clone.setCustomerName(getCustomerName());
        clone.setCustomerPhone(getCustomerPhone());
        clone.setReadOnly(isReadOnly());
        clone.setForceToPrint(isForceToPrint());
        clone.setStatus(getStatus());
        clone.setSource(getSource());
        clone.setPayments(new HashMap<>());
        getPayments().forEach(clone.getPayments()::put);
        clone.setBranchId(getBranchId());
        clone.setBranch(getBranch());
        clone.setCompanyId(getCompanyId());
        clone.setCompany(getCompany());
        clone.setCreatedById(getCreatedById());
        clone.setCreatedBy(getCreatedBy());
        clone.setTerminalId(getTerminalId());
        clone.setTerminal(getTerminal());
        Set<ReceiptDetail> detailClones = getReceiptDetails().stream().map(receiptDetail -> {
            ReceiptDetail detailClone = receiptDetail.clone();
            detailClone.setReceipt(clone);
            return detailClone;
        }).collect(Collectors.toSet());
        clone.setReceiptDetails(detailClones);
        clone.setAdditional(getAdditional());
        clone.setBillId(getBillId());
        clone.setFacturaId(getFacturaId());
        clone.setSendSms(isSendSms());
        return clone;
    }
}
