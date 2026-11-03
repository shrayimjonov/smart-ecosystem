package uz.uzkassa.smartpossupply.domain.factura;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.type.SqlTypes;
import uz.uzkassa.smartpossupply.domain.Branch;
import uz.uzkassa.smartpossupply.domain.base.AbstractAuditingEntity;
import uz.uzkassa.smartpossupply.domain.company.Company;
import uz.uzkassa.smartpossupply.domain.stock.Stock;
import uz.uzkassa.smartpossupply.dto.eimzo.SignatureData;
import uz.uzkassa.smartpossupply.dto.factura.*;
import uz.uzkassa.smartpossupply.dto.factura.supply.FacturaLinkDTO;
import uz.uzkassa.smartpossupply.dto.factura.supply.SupplyFacturaDTO;
import uz.uzkassa.smartpossupply.enums.AppType;
import uz.uzkassa.smartpossupply.enums.EntityTypeEnum;
import uz.uzkassa.smartpossupply.enums.TransferType;
import uz.uzkassa.smartpossupply.interfaces.Auditable;
import uz.uzkassa.smartpossupply.utils.DateUtils;

import java.io.Serial;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

@SqlResultSetMappings(
        {
                @SqlResultSetMapping(
                        name = "FacturaStatisticByDayMapper",
                        classes = @ConstructorResult(
                                targetClass = FacturaStatisticsDTO.class,
                                columns = {
                                        @ColumnResult(name = "amount", type = BigDecimal.class),
                                        @ColumnResult(name = "byday", type = Long.class),
                                        @ColumnResult(name = "byweek", type = Long.class),
                                        @ColumnResult(name = "bymonth", type = Long.class),
                                        @ColumnResult(name = "byyear", type = Long.class),
                                }
                        )
                ),
                @SqlResultSetMapping(
                        name = "FacturaStatisticByWeekMapper",
                        classes = @ConstructorResult(
                                targetClass = FacturaStatisticsDTO.class,
                                columns = {
                                        @ColumnResult(name = "amount", type = BigDecimal.class),
                                        @ColumnResult(name = "byweek", type = Long.class),
                                        @ColumnResult(name = "bymonth", type = Long.class),
                                        @ColumnResult(name = "byyear", type = Long.class),
                                }
                        )
                ),
                @SqlResultSetMapping(
                        name = "FacturaStatisticByMonthMapper",
                        classes = @ConstructorResult(
                                targetClass = FacturaStatisticsDTO.class,
                                columns = {
                                        @ColumnResult(name = "amount", type = BigDecimal.class),
                                        @ColumnResult(name = "bymonth", type = Long.class),
                                        @ColumnResult(name = "byyear", type = Long.class),
                                }
                        )
                )
        }
)
@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
@Entity
@Table(name = "factura",
        indexes = {
                @Index(columnList = "buyer_id", name = "factura_buyer_id_idx"),
                @Index(columnList = "seller_id", name = "factura_seller_id_idx"),
                @Index(columnList = "company_id", name = "factura_company_id_idx"),
                @Index(columnList = "factura_no", name = "factura_factura_no_idx"),
                @Index(columnList = "factura_date", name = "factura_factura_date_idx"),
                @Index(columnList = "factura_id", name = "factura_factura_id_idx"),
                @Index(columnList = "outgoing", name = "factura_outgoing_idx"),
                @Index(columnList = "branch_id", name = "factura_branch_id_idx")
        })
@SQLDelete(sql = "UPDATE factura SET deleted = true WHERE id=?")
@Slf4j
@ToString
public class Factura extends AbstractAuditingEntity implements Auditable {

    @Serial
    private static final long serialVersionUID = 349583495843L;

    @Column(name = "increment_number")
    Long incrementNumber;

    @Column(name = "has_financial_discount", columnDefinition = "boolean default false")
    boolean hasFinancialDiscount = false;

    @Column(name = "outgoing", columnDefinition = "boolean default false")
    boolean outgoing = false;

    @Column(name = "version", columnDefinition = "int default 1")
    Integer version = 1;

    @Column(name = "branch_id")
    Long branchId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "branch_id", updatable = false, insertable = false)
    Branch branch;

    @Column(name = "factura_type")
    Integer facturaType;

    @Column(name = "single_sided_type")
    Integer singleSidedType;

    @Column(name = "factura_id", unique = true)
    String facturaId;

    @Column(name = "factura_no")
    String facturaNo;

    @Column(name = "factura_date")
    LocalDate facturaDate;

    @Column(name = "factura_product_id")
    String facturaProductId;

    @Column(name = "buyer_id")
    Long buyerId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "buyer_id", updatable = false, insertable = false)
    Company buyer;

    @Column(name = "seller_id")
    Long sellerId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "seller_id", updatable = false, insertable = false)
    Company seller;

    @Column(name = "company_id")
    Long companyId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "company_id", updatable = false, insertable = false)
    Company company;

    @Column(name = "stock_id")
    Long stockId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "stock_id", updatable = false, insertable = false)
    Stock stock;

    @Column(name = "stock_in_id")
    Long stockInId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "stock_in_id", updatable = false, insertable = false)
    Stock stockIn;

    @JdbcTypeCode(SqlTypes.LONGVARCHAR)
    @Column(name = "sign")
    String sign;

    @Column(name = "total", precision = 25, scale = 2)
    BigDecimal total;

    @Column(name = "total_lgota_vat_sum", precision = 25, scale = 2)
    BigDecimal totalLgotaVatSum;

    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    Status status = Status.DRAFT;

    @JdbcTypeCode(SqlTypes.LONGVARCHAR)
    @Column(name = "notes")
    String notes;

    @Column(name = "send_number")
    String sendNumber;

    @Column(name = "send_by")
    String sendBy;

    @Column(name = "send_date")
    LocalDateTime sendDate;

    @Column(name = "approved_number")
    String approvedNumber;

    @Column(name = "approved_by")
    String approvedBy;

    @Column(name = "approved_date")
    LocalDateTime approvedDate;

    @Column(name = "agent_approved_number")
    String agentApprovedNumber;

    @Column(name = "agent_approved_by")
    String agentApprovedBy;

    @Column(name = "agent_approved_date")
    LocalDateTime agentApprovedDate;

    @JdbcTypeCode(SqlTypes.LONGVARCHAR)
    @Column(name = "error_message")
    String errorMessage;

    @Enumerated(EnumType.STRING)
    @Column(name = "app_type")
    AppType appType;

    //FacturaContract
    @Column(name = "contract_no")
    String contractNo;

    @Column(name = "contract_date")
    LocalDate contractDate;

    @OneToMany(mappedBy = "factura", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    Set<FacturaDetail> facturaDetails = new HashSet<>();

    public Set<FacturaDetail> getFacturaDetails() {
        return facturaDetails;
    }

    public FacturaDetail getFacturaDetail() {
        if (CollectionUtils.isEmpty(facturaDetails)) {
            return null;
        }
        return new ArrayList<>(facturaDetails).getFirst();
    }

    @Override
    public String getName() {
        return this.getFacturaNo();
    }

    @Override
    public Long getEntityId() {
        return this.id;
    }

    @Override
    public EntityTypeEnum getEntityType() {
        return EntityTypeEnum.FACTURA;
    }

    @Override
    public String getEntityName() {
        return getName();
    }


    public FacturaLinkDTO toLink() {
        FacturaLinkDTO facturaLinkDTO = new FacturaLinkDTO();
        facturaLinkDTO.setId(getId());
        facturaLinkDTO.setFacturaId(getFacturaId());
        facturaLinkDTO.setFacturaNo(getFacturaNo());
        facturaLinkDTO.setStatus(getStatus());
        facturaLinkDTO.setStatusName(getStatus().getName());
        facturaLinkDTO.setFacturaDate(getFacturaDate());
        return facturaLinkDTO;
    }

    public SupplyFacturaDTO toDTO() {
        SupplyFacturaDTO supplyFacturaDTO = new SupplyFacturaDTO();
        supplyFacturaDTO.setId(getId());
        supplyFacturaDTO.setStatus(getStatus());
        supplyFacturaDTO.setStatusName(getStatus().getName());

        supplyFacturaDTO.setNotes(getNotes());
        supplyFacturaDTO.setTotal(getTotal());
        supplyFacturaDTO.setTotalLgotaVatSum(getTotalLgotaVatSum());
        supplyFacturaDTO.setErrorMessage(getErrorMessage());

        supplyFacturaDTO.setSendNumber(getSendNumber());
        supplyFacturaDTO.setSendBy(getSendBy());
        supplyFacturaDTO.setSendDate(getSendDate());

        supplyFacturaDTO.setAgentApprovedNumber(getAgentApprovedNumber());
        supplyFacturaDTO.setAgentApprovedBy(getAgentApprovedBy());
        supplyFacturaDTO.setAgentApprovedDate(getAgentApprovedDate());

        supplyFacturaDTO.setApprovedNumber(getApprovedNumber());
        supplyFacturaDTO.setApprovedBy(getApprovedBy());
        supplyFacturaDTO.setApprovedDate(getApprovedDate());

        supplyFacturaDTO.setAppliedFinancialDiscount(isHasFinancialDiscount());
        supplyFacturaDTO.setAppType(getAppType());
        if (getBranch() != null) {
            supplyFacturaDTO.setBranch(getBranch().toCommonDTO());
        }
        if (getStock() != null) {
            supplyFacturaDTO.setStock(getStock().toStockCommonDTO());
            supplyFacturaDTO.setAdvancePaymentAmount(getStock().getAdvancePaymentAmount());
            supplyFacturaDTO.setAdvancePaymentPercent(getStock().getAdvancePaymentPercent());
            supplyFacturaDTO.setSpecificationDate(getStock().getSpecificationDate());
            supplyFacturaDTO.setSpecificationNumber(getStock().getSpecificationNumber());
        }
        if (getStockIn() != null) {
            supplyFacturaDTO.setStockIn(getStockIn().toStockCommonDTO());
        } else if (getStock() != null && TransferType.IN == getStock().getTransferType()) {
            supplyFacturaDTO.setStockIn(getStock().toStockCommonDTO());
        }
        supplyFacturaDTO.setBuyerId(getBuyerId());
        supplyFacturaDTO.setSellerId(getSellerId());

        return supplyFacturaDTO;
    }

    public void fromFacturaDocDTO(FacturaDocDTO facturaDocDTO) {
        setFacturaNo(facturaDocDTO.getFacturaNo());
        setFacturaDate(facturaDocDTO.getFacturaDate());
    }

    public FacturaDTO toFacturaDTO() {
        return this.toFacturaDTO(true);
    }

    public FacturaDTO toFacturaDTO(boolean withProduct) {
        if (getFacturaDetail() != null) {
            FacturaDTO facturaDTO = getFacturaDetail().toFacturaDTO(withProduct);
            facturaDTO.setFacturaType(getFacturaType());
            facturaDTO.setSingleSidedType(getSingleSidedType());
            facturaDTO.setFacturaId(getFacturaId());
            facturaDTO.setSellerTin(getSeller().getTin());
            facturaDTO.setSeller(getFacturaDetail().toSellerDTO(getSeller()));
            if (getBuyer() != null) {
                facturaDTO.setBuyerTin(getBuyer().getTin());
                facturaDTO.setBuyer(getFacturaDetail().toBuyerDTO(getBuyer()));
            }
            return facturaDTO;
        }
        return null;
    }

    public void setSenderData(SignatureData signatureData) {
        setSendNumber(signatureData.getNumber());
        setSendBy(signatureData.getOwner());
        setSendDate(signatureData.getDateTime());
    }

    public void setApproverData(SignatureData signatureData) {
        setApprovedNumber(signatureData.getNumber());
        setApprovedBy(signatureData.getOwner());
        setApprovedDate(signatureData.getDateTime());
    }

    public void fromContractDTO(FacturaContractDTO facturaContractDTO) {
        try {
            setContractNo(facturaContractDTO.getContractNo());
            LocalDate contractDate;
            if (facturaContractDTO.getContractDate().contains("T")) {
                contractDate = LocalDate.parse(facturaContractDTO.getContractDate(), DateUtils.YEAR_MONTH_DAY_TIMEZONE_FORMATTER);
            } else if (facturaContractDTO.getContractDate().contains(" ")) {
                contractDate = LocalDate.parse(facturaContractDTO.getContractDate(), DateUtils.DATE_TIME_HYPHEN_FORMATTER);
            } else {
                contractDate = LocalDate.parse(facturaContractDTO.getContractDate(), DateUtils.YEAR_MONTH_DAY_HYPHEN_FORMATTER);
            }
            setContractDate(contractDate);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
    }
}
