package uz.uzkassa.smartpossupply.domain.stock;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import org.hibernate.annotations.BatchSize;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;
import org.hibernate.type.SqlTypes;
import uz.uzkassa.smartpossupply.domain.Branch;
import uz.uzkassa.smartpossupply.domain.acceptancetransferact.AcceptanceTransferAct;
import uz.uzkassa.smartpossupply.domain.aggregation.Aggregation;
import uz.uzkassa.smartpossupply.domain.aic.Aic;
import uz.uzkassa.smartpossupply.domain.auth.User;
import uz.uzkassa.smartpossupply.domain.base.AbstractAuditingEntity;
import uz.uzkassa.smartpossupply.domain.company.Company;
import uz.uzkassa.smartpossupply.domain.company.CompanyCommittent;
import uz.uzkassa.smartpossupply.domain.customerbranch.CustomerBranch;
import uz.uzkassa.smartpossupply.domain.factura.Contract;
import uz.uzkassa.smartpossupply.domain.factura.Factura;
import uz.uzkassa.smartpossupply.domain.mark.MarkOrderParty;
import uz.uzkassa.smartpossupply.domain.order.Order;
import uz.uzkassa.smartpossupply.domain.party.Party;
import uz.uzkassa.smartpossupply.domain.pricelist.PriceList;
import uz.uzkassa.smartpossupply.domain.receipt.Receipt;
import uz.uzkassa.smartpossupply.domain.stocktransfer.StockTransfer;
import uz.uzkassa.smartpossupply.domain.warehouse.Vehicle;
import uz.uzkassa.smartpossupply.domain.warehouse.Warehouse;
import uz.uzkassa.smartpossupply.dto.stock.StockCommonDTO;
import uz.uzkassa.smartpossupply.dto.stock.StockProductLookUpDTO;
import uz.uzkassa.smartpossupply.dto.stockreport.StockProductStatisticsDTO;
import uz.uzkassa.smartpossupply.dto.stockreport.StockReportDTO;
import uz.uzkassa.smartpossupply.dto.stockreport.StockStatisticsDTO;
import uz.uzkassa.smartpossupply.enums.*;
import uz.uzkassa.smartpossupply.interfaces.Auditable;
import uz.uzkassa.smartpossupply.interfaces.Relation;

import java.io.Serial;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;


@SqlResultSetMappings(
        {
                @SqlResultSetMapping(
                        name = "StockProductLookUpMapper",
                        classes = @ConstructorResult(
                                targetClass = StockProductLookUpDTO.class,
                                columns = {
                                        @ColumnResult(name = "id", type = Long.class),
                                        @ColumnResult(name = "name", type = String.class),

                                        @ColumnResult(name = "qtyInWarehouse", type = BigDecimal.class),
                                        @ColumnResult(name = "reservationQty", type = BigDecimal.class),
                                        @ColumnResult(name = "aggregationOrMarkQty", type = BigDecimal.class),

                                        @ColumnResult(name = "qtyInPackage", type = BigDecimal.class),
                                        @ColumnResult(name = "qtyInBox", type = BigDecimal.class),
                                        @ColumnResult(name = "qtyInPallet", type = BigDecimal.class),

                                        @ColumnResult(name = "purchasePrice", type = BigDecimal.class),
                                        @ColumnResult(name = "price", type = BigDecimal.class),

                                        @ColumnResult(name = "exciseRate", type = BigDecimal.class),
                                        @ColumnResult(name = "exciseSum", type = BigDecimal.class),

                                        @ColumnResult(name = "vatCode", type = String.class),
                                        @ColumnResult(name = "barcode", type = String.class),
                                        @ColumnResult(name = "vatBarcode", type = String.class),
                                        @ColumnResult(name = "catalogName", type = String.class),
                                        @ColumnResult(name = "packageName", type = String.class),
                                        @ColumnResult(name = "packageCode", type = String.class),

                                        @ColumnResult(name = "capacity", type = String.class),
                                        @ColumnResult(name = "hasMark", type = Boolean.class),

                                        @ColumnResult(name = "productTypeCode", type = String.class),
                                        @ColumnResult(name = "blockProducts", type = String.class),

                                        @ColumnResult(name = "sortOrder", type = Integer.class),

                                        @ColumnResult(name = "companyCommittentId", type = Long.class),
                                        @ColumnResult(name = "committentId", type = Long.class),
                                        @ColumnResult(name = "committentName", type = String.class),
                                        @ColumnResult(name = "committentTin", type = String.class),

                                        @ColumnResult(name = "parentId", type = Long.class),
                                        @ColumnResult(name = "parentName", type = String.class),
                                        @ColumnResult(name = "parentBarcode", type = String.class),
                                        @ColumnResult(name = "parentVatBarcode", type = String.class),

                                        @ColumnResult(name = "boxBarcode", type = String.class),
                                        @ColumnResult(name = "blockBarcode", type = String.class),

                                        @ColumnResult(name = "exchangeRate", type = BigDecimal.class),
                                        @ColumnResult(name = "currencyType", type = String.class),
                                        @ColumnResult(name = "foreignPrice", type = BigDecimal.class)
                                }
                        )
                ),
                @SqlResultSetMapping(
                        name = "StockReportMapper",
                        classes = @ConstructorResult(
                                targetClass = StockReportDTO.class,
                                columns = {
                                        @ColumnResult(name = "id", type = Long.class),
                                        @ColumnResult(name = "name", type = String.class),
                                        @ColumnResult(name = "sellingPrice", type = BigDecimal.class),
                                        @ColumnResult(name = "totalSellingPrice", type = BigDecimal.class),
                                        @ColumnResult(name = "purchasePrice", type = BigDecimal.class),
                                        @ColumnResult(name = "totalPurchasePrice", type = BigDecimal.class),
                                        @ColumnResult(name = "potentialProfit", type = BigDecimal.class),
                                        @ColumnResult(name = "potentialProfitPercent", type = BigDecimal.class),
                                        @ColumnResult(name = "totalProfit", type = BigDecimal.class),
                                        @ColumnResult(name = "soldQty", type = BigDecimal.class),
                                        @ColumnResult(name = "markUpPercent", type = Long.class),
                                }
                        )
                ),
                @SqlResultSetMapping(
                        name = "StockStatisticsMapper",
                        classes = @ConstructorResult(
                                targetClass = StockStatisticsDTO.class,
                                columns = {@ColumnResult(name = "dataType", type = String.class), @ColumnResult(name = "total", type = Long.class)}
                        )
                ),
                @SqlResultSetMapping(
                        name = "StockProductQtyStatisticsMapper",
                        classes = @ConstructorResult(
                                targetClass = StockProductStatisticsDTO.class,
                                columns = {
                                        @ColumnResult(name = "qty", type = BigDecimal.class),
                                        @ColumnResult(name = "dataType", type = String.class),
                                }
                        )
                )
        }
)
@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
@Entity
@Table(
        name = "stock",
        indexes = {
                @Index(columnList = "supplier_id", name = "stock_supplier_id_idx"),
                @Index(columnList = "company_id", name = "stock_company_id_idx"),
                @Index(columnList = "branch_id", name = "stock_branch_id_idx"),
                @Index(columnList = "warehouse_id", name = "stock_warehouse_id_idx"),
                @Index(columnList = "stock_number", name = "stock_number_idx"),
                @Index(columnList = "status", name = "stock_status_idx"),
                @Index(columnList = "deleted", name = "stock_deleted_idx"),
                @Index(columnList = "company_id, transfer_type, deleted, status", name = "stock_company_id_transfer_type_deleted_status_idx")
        }
)
@SQLDelete(sql = "UPDATE stock SET deleted = true WHERE id=?")
public class Stock extends AbstractAuditingEntity implements Relation, Auditable {

    @Serial
    private static final long serialVersionUID = 98756L;

    @Column(name = "stock_number")
    String stockNumber;

    @Column(name = "increment_number")
    Long incrementNumber;

    @Column(name = "contract_id")
    Long contractId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "contract_id", updatable = false, insertable = false)
    Contract contract;

    @Column(name = "transfer_date")
    LocalDateTime transferDate;

    @Column(name = "completed_date")
    LocalDateTime completedDate;

    @Column(name = "warehouse_id")
    Long warehouseId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "warehouse_id", updatable = false, insertable = false)
    Warehouse warehouse;

    @Column(name = "customer_id")
    Long customerId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "customer_id", updatable = false, insertable = false)
    Company customer;

    @Column(name = "supplier_id")
    Long supplierId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "supplier_id", updatable = false, insertable = false)
    Company supplier;

    @Column(name = "company_committent_id")
    Long companyCommittentId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "company_committent_id", updatable = false, insertable = false)
    CompanyCommittent companyCommittent;

    @Column(name = "company_id")
    Long companyId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "company_id", updatable = false, insertable = false)
    Company company;

    @Column(name = "branch_id")
    Long branchId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "branch_id", updatable = false, insertable = false)
    Branch branch;

    @Column(name = "vehicle_id")
    Long vehicleId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "vehicle_id", updatable = false, insertable = false)
    Vehicle vehicle;

    @Column(name = "factura_id")
    Long facturaId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "factura_id", updatable = false, insertable = false)
    Factura factura;

    @Column(name = "party_id")
    Long partyId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "party_id", updatable = false, insertable = false)
    Party party;

    @Column(name = "mark_order_party_id")
    Long markOrderPartyId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "mark_order_party_id", updatable = false, insertable = false)
    MarkOrderParty markOrderParty;

    @Column(name = "aic_id")
    Long aicId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "aic_id", updatable = false, insertable = false)
    Aic aic;

    @Column(name = "order_id")
    Long orderId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id", updatable = false, insertable = false)
    Order order;

    @Enumerated(EnumType.STRING)
    @Column(name = "transfer_type")
    TransferType transferType;

    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    StockStatus status = StockStatus.DRAFT;

    @Column(name = "old_stock_id")
    Long oldStockId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "old_stock_id", updatable = false, insertable = false)
    Stock oldStock;

    @Column(name = "stock_in_id")
    Long stockInId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "stock_in_id", updatable = false, insertable = false)
    Stock stockIn;

    @Column(name = "stock_transfer_id")
    Long stockTransferId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "stock_transfer_id", updatable = false, insertable = false)
    StockTransfer stockTransfer;

    @Column(name = "price_list_id")
    Long priceListId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "price_list_id", updatable = false, insertable = false)
    PriceList priceList;

    @Column(name = "acceptance_transfer_act_id")
    Long acceptanceTransferActId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "acceptance_transfer_act_id", updatable = false, insertable = false)
    AcceptanceTransferAct acceptanceTransferAct;

    @Column(name = "delivery_branch_id")
    Long deliveryBranchId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "delivery_branch_id", updatable = false, insertable = false)
    CustomerBranch deliveryBranch;

    @Column(name = "cashier_id")
    Long cashierId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cashier_id", updatable = false, insertable = false)
    User cashier;

    @Column(name = "total_qty", precision = 25, scale = 2)
    BigDecimal totalQty;

    @Column(name = "total_price", precision = 25, scale = 2)
    BigDecimal totalPrice;

    @Column(name = "total_foreign_price", precision = 25, scale = 2)
    BigDecimal totalForeignPrice;

    @Column(name = "total_price_withount_vat", precision = 25, scale = 2)
    BigDecimal totalPriceWithoutVat;

    @Column(name = "total_vat_price", precision = 25, scale = 2)
    BigDecimal totalVatPrice;

    @Column(name = "total_brutto", precision = 25, scale = 6)
    BigDecimal totalBrutto;

    @Column(name = "total_netto", precision = 25, scale = 6)
    BigDecimal totalNetto;

    @JdbcTypeCode(SqlTypes.LONGVARCHAR)
    @Column(name = "description")
    String description;

    @Column(name = "clickhouse_sync", columnDefinition = "boolean default false")
    boolean clickhouseSync = false;

    @Column(name = "advance_payment_amount", precision = 25, scale = 2)
    BigDecimal advancePaymentAmount;

    @Column(name = "advance_payment_percent", precision = 25, scale = 2)
    BigDecimal advancePaymentPercent;

    @Column(name = "total_discount_price", precision = 25, scale = 2)
    BigDecimal totalDiscountPrice;

    @Column(name = "exchange_rate", precision = 25, scale = 2)
    BigDecimal exchangeRate;

    @Enumerated(EnumType.STRING)
    @Column(name = "currency_type")
    CurrencyType currencyType;

    @Column(name = "specification_number")
    String specificationNumber;

    @Column(name = "specification_date")
    LocalDate specificationDate;

    @Enumerated(EnumType.STRING)
    @Column(name = "app_type")
    AppType appType;

    @Column(name = "with_limit", columnDefinition = "boolean default false")
    boolean withLimit = false;

    @Column(name = "one_c_sync", columnDefinition = "boolean default false")
    boolean oneCSync = false;

    @Column(name = "has_discount", columnDefinition = "boolean default false")
    boolean hasDiscount = false;

    @Column(name = "receipt_uid")
    String receiptUid;

    @Enumerated(EnumType.STRING)
    @Column(name = "payment_type")
    PaymentType paymentType;

    @ManyToMany
    @JoinTable(
            name = "stock_factura",
            joinColumns = {@JoinColumn(name = "stock_id", referencedColumnName = "id")},
            inverseJoinColumns = {@JoinColumn(name = "factura_id", referencedColumnName = "id")}
    )
    @SQLRestriction("deleted = false")
    @BatchSize(size = 50)
    Set<Factura> facturas = new HashSet<>();

    @ManyToMany
    @JoinTable(
            name = "stock_receipt",
            joinColumns = {@JoinColumn(name = "stock_id", referencedColumnName = "id")},
            inverseJoinColumns = {@JoinColumn(name = "receipt_id", referencedColumnName = "id")}
    )
    @SQLRestriction("deleted = false")
    Set<Receipt> receipts = new HashSet<>();

    @OneToMany(mappedBy = "stock", cascade = CascadeType.ALL, orphanRemoval = true)
    @SQLRestriction("deleted = false")
    @OrderBy("sOrder,id")
    Set<StockProduct> products = new HashSet<>();

    @OneToMany(mappedBy = "stock", cascade = CascadeType.ALL, orphanRemoval = true)
    List<Aggregation> aggregations = new ArrayList<>();

    public void addProduct(StockProduct stockProduct) {
        stockProduct.setStock(this);
        stockProduct.setStockId(this.id);
        products.add(stockProduct);
    }

    @Override
    public String getName() {
        return this.getStockNumber();
    }

    @Override
    public Long getEntityId() {
        return this.id;
    }

    @Override
    public EntityTypeEnum getEntityType() {
        if (getTransferType() == null) {
            return EntityTypeEnum.STOCK;
        }
        return switch (getTransferType()) {
            case IN -> EntityTypeEnum.STOCK_IN;
            case OUT -> EntityTypeEnum.STOCK_OUT;
            case RETURN -> EntityTypeEnum.STOCK_RETURN;
            default -> EntityTypeEnum.STOCK;
        };
    }

    @Override
    public Long getRelationId() {
        if (getFacturaId() != null) {
            return facturaId;
        }
        if (getPartyId() != null) {
            return partyId;
        }
        if (getAicId() != null) {
            return aicId;
        }
        if (getAcceptanceTransferActId() != null) {
            return acceptanceTransferActId;
        }
        return null;
    }

    @Override
    public RelationType getRelationType() {
        if (getFacturaId() != null) {
            return RelationType.FACTURA;
        }
        if (getPartyId() != null) {
            return RelationType.AGGREGATION;
        }
        if (getAicId() != null) {
            return RelationType.AIC;
        }
        if (getAcceptanceTransferActId() != null) {
            return RelationType.ACT;
        }
        return RelationType.MANUAL;
    }

    @Override
    public String getRelationNumber() {
        if (getFacturaId() != null) {
            return factura.getFacturaNo();
        }
        if (getPartyId() != null) {
            return party.getPartyNumber();
        }
        if (getAicId() != null) {
            return aic.getReportId();
        }
        if (getAcceptanceTransferAct() != null) {
            return acceptanceTransferAct.getActNo();
        }
        return null;
    }

    public StockCommonDTO toStockCommonDTO() {
        return new StockCommonDTO(this.getId(), this.getName(), this.getTransferType(), this.getStatus());
    }

    public void setStockNumber(String stockNumber) {
        addLogHistory("stockNumber", this.stockNumber, stockNumber);
        this.stockNumber = stockNumber;
    }

    public void setTransferDate(LocalDateTime transferDate) {
        addLogHistory("transferDate", this.transferDate, transferDate);
        this.transferDate = transferDate;
    }

    public void setCompletedDate(LocalDateTime completedDate) {
        addLogHistory("completedDate", this.completedDate, completedDate);
        this.completedDate = completedDate;
    }

    public void setWarehouse(Warehouse warehouse) {
        addLogHistory("warehouse", this.warehouse != null ? this.warehouse.getName() : null, warehouse != null ? warehouse.getName() : null);
        this.warehouse = warehouse;
    }

    public void setCompanyCommittent(CompanyCommittent companyCommittent) {
        addLogHistory("companyCommittent", this.companyCommittent != null && this.companyCommittent.getCommittent() != null ? this.companyCommittent.getCommittent().getName() : null, companyCommittent != null && companyCommittent.getCommittent() != null ? companyCommittent.getCommittent().getName() : null);
        this.companyCommittent = companyCommittent;
    }

    public void setBranch(Branch branch) {
        addLogHistory("branch", this.branch != null ? this.branch.getName() : null, branch != null ? branch.getName() : null);
        this.branch = branch;
    }

    public void setFactura(Factura factura) {
        addLogHistory("factura", this.factura != null ? this.factura.getName() : null, factura != null ? factura.getName() : null);
        this.factura = factura;
    }

    public void setParty(Party party) {
        addLogHistory("party", this.party != null ? this.party.getName() : null, party != null ? party.getName() : null);
        this.party = party;
    }

    public void setOrder(Order order) {
        addLogHistory("order", this.order != null ? this.order.getName() : null, order != null ? order.getName() : null);
        this.order = order;
    }

    public void setTransferType(TransferType transferType) {
        addLogHistory("transferType", this.transferType != null ? this.transferType.name() : null, transferType != null ? transferType.name() : null);
        this.transferType = transferType;
    }

    public void setStatus(StockStatus status) {
        if (StockStatus.DRAFT == status && StockStatus.DRAFT != this.status && StockStatus.CHECKING != this.status) {
            addLogHistory("errorStatus", this.status != null ? this.status.getName() : null, status.getName());
            return;
        }
        addLogHistory("status", this.status != null ? this.status.getName() : null, status != null ? status.getName() : null);
        this.status = status;
    }

    public void setPriceList(PriceList priceList) {
        addLogHistory("priceList", this.priceList != null ? this.priceList.getName() : null, priceList != null ? priceList.getName() : null);
        this.priceList = priceList;
    }

    public void setAcceptanceTransferAct(AcceptanceTransferAct acceptanceTransferAct) {
        addLogHistory("acceptanceTransferAct", this.acceptanceTransferAct != null ? this.acceptanceTransferAct.getName() : null, acceptanceTransferAct != null ? acceptanceTransferAct.getName() : null);
        this.acceptanceTransferAct = acceptanceTransferAct;
    }

    public void setDeliveryBranch(CustomerBranch deliveryBranch) {
        addLogHistory("deliveryBranch", this.deliveryBranch != null ? this.deliveryBranch.getName() : null, deliveryBranch != null ? deliveryBranch.getName() : null);
        this.deliveryBranch = deliveryBranch;
    }

    public void setTotalQty(BigDecimal totalQty) {
        addLogHistory("totalQty", this.totalQty, totalQty);
        this.totalQty = totalQty;
    }

    public void setTotalPrice(BigDecimal totalPrice) {
        addLogHistory("totalPrice", this.totalPrice, totalPrice);
        this.totalPrice = totalPrice;
    }

    public void setTotalPriceWithoutVat(BigDecimal totalPriceWithoutVat) {
        addLogHistory("totalPriceWithoutVat", this.totalPriceWithoutVat, totalPriceWithoutVat);
        this.totalPriceWithoutVat = totalPriceWithoutVat;
    }

    public void setTotalVatPrice(BigDecimal totalVatPrice) {
        addLogHistory("totalVatPrice", this.totalVatPrice, totalVatPrice);
        this.totalVatPrice = totalVatPrice;
    }

    public void setDescription(String description) {
        addLogHistory("description", this.description, description);
        this.description = description;
    }

    public void setClickhouseSync(boolean clickhouseSync) {
        addLogHistory("clickhouseSync", this.clickhouseSync, clickhouseSync);
        this.clickhouseSync = clickhouseSync;
    }
}
