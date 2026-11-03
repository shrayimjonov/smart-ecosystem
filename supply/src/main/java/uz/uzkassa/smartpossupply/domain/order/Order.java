package uz.uzkassa.smartpossupply.domain.order;

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
import uz.uzkassa.smartpossupply.domain.address.Address;
import uz.uzkassa.smartpossupply.domain.auth.User;
import uz.uzkassa.smartpossupply.domain.base.AbstractAuditingEntity;
import uz.uzkassa.smartpossupply.domain.company.Company;
import uz.uzkassa.smartpossupply.domain.customerbranch.CustomerBranch;
import uz.uzkassa.smartpossupply.domain.factura.Contract;
import uz.uzkassa.smartpossupply.domain.planroute.PlanRoute;
import uz.uzkassa.smartpossupply.domain.planroute.PlanRouteCustomerBranch;
import uz.uzkassa.smartpossupply.domain.pricelist.PriceList;
import uz.uzkassa.smartpossupply.domain.stock.Stock;
import uz.uzkassa.smartpossupply.domain.stocktransfer.StockTransfer;
import uz.uzkassa.smartpossupply.domain.trip.Trip;
import uz.uzkassa.smartpossupply.domain.warehouse.Vehicle;
import uz.uzkassa.smartpossupply.domain.warehouse.Warehouse;
import uz.uzkassa.smartpossupply.dto.order.OrderBaseDTO;
import uz.uzkassa.smartpossupply.dto.order.OrderStatisticsItemDTO;
import uz.uzkassa.smartpossupply.enums.*;
import uz.uzkassa.smartpossupply.interfaces.Auditable;

import java.io.Serial;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@SqlResultSetMappings(
        @SqlResultSetMapping(
                name = "MobileOrderStatisticsMapper",
                classes = @ConstructorResult(
                        targetClass = OrderStatisticsItemDTO.class,
                        columns = {
                                @ColumnResult(name = "orderTotalCount", type = Long.class),
                                @ColumnResult(name = "orderTotalAmount", type = BigDecimal.class),
                                @ColumnResult(name = "type", type = String.class),
                        }
                )
        )
)
@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
@Entity
@Table(
        name = "orders",
        indexes = {
                @Index(columnList = "company_id", name = "orders_company_id_idx"),
                @Index(columnList = "customer_id", name = "orders_customer_id_idx"),
                @Index(columnList = "supplier_id", name = "orders_supplier_id_idx"),
                @Index(columnList = "branch_id", name = "orders_branch_id_idx"),
                @Index(columnList = "warehouse_id", name = "orders_warehouse_id_idx"),
                @Index(columnList = "order_number", name = "orders_order_number_idx"),
        }
)
@SQLDelete(sql = "UPDATE orders SET deleted = true WHERE id=?")
//@SQLRestriction(value = "deleted = false")
public class Order extends AbstractAuditingEntity implements Auditable {

    @Serial
    private static final long serialVersionUID = 10900323L;

    @Column(name = "order_number")
    String orderNumber;

    @Column(name = "increment_number")
    Long incrementNumber;

    @Column(name = "order_date")
    LocalDateTime orderDate;

    @Column(name = "market_code")
    String marketCode;

    @Column(name = "contract_id")
    Long contractId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "contract_id", updatable = false, insertable = false)
    Contract contract;

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

    @Column(name = "warehouse_id")
    Long warehouseId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "warehouse_id", updatable = false, insertable = false)
    Warehouse warehouse;

    @Column(name = "vehicle_id")
    Long vehicleId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "vehicle_id", updatable = false, insertable = false)
    Vehicle vehicle;

    @Column(name = "stock_in_id")
    Long stockInId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "stock_in_id", updatable = false, insertable = false)
    Stock stockIn;

    @Column(name = "stock_out_id")
    Long stockOutId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "stock_out_id", updatable = false, insertable = false)
    Stock stockOut;

    @ManyToMany
    @JoinTable(
            name = "order_stock",
            joinColumns = {@JoinColumn(name = "order_id", referencedColumnName = "id")},
            inverseJoinColumns = {@JoinColumn(name = "stock_id", referencedColumnName = "id")}
    )
    @SQLRestriction("deleted = false")
    @BatchSize(size = 50)
    Set<Stock> stockOuts = new HashSet<>();

    @Column(name = "stock_out_number")
    String stockOutNumber;

    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    OrderStatus status = OrderStatus.NEW;

    @Column(name = "reservation_released")
    boolean reservationReleased = false;

    @Enumerated(EnumType.STRING)
    @Column(name = "sale_type")
    SaleType saleType;

    @Column(name = "advance_payment_amount", precision = 25, scale = 2)
    BigDecimal advancePaymentAmount;

    @Column(name = "advance_payment_percent", precision = 25, scale = 2)
    BigDecimal advancePaymentPercent;

    @Enumerated(EnumType.STRING)
    @Column(name = "payment_type")
    PaymentType paymentType;

    @JdbcTypeCode(SqlTypes.LONGVARCHAR)
    @Column(name = "description")
    String description;

    @Column(name = "delivery_branch_id")
    Long deliveryBranchId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "delivery_branch_id", updatable = false, insertable = false)
    CustomerBranch deliveryBranch;

    @Embedded
    Address deliveryAddress;

    @Column(name = "total_qty", precision = 25, scale = 2)
    BigDecimal totalQty;

    @Column(name = "sku", precision = 25, scale = 2)
    BigDecimal sku;

    @Column(name = "total_price", precision = 25, scale = 2)
    BigDecimal totalPrice;

    @Column(name = "exchange_rate", precision = 25, scale = 2)
    BigDecimal exchangeRate;

    @Enumerated(EnumType.STRING)
    @Column(name = "currency_type")
    CurrencyType currencyType;

    @Column(name = "total_foreign_price", precision = 25, scale = 2)
    BigDecimal totalForeignPrice;

    @Column(name = "total_price_without_vat", precision = 25, scale = 2)
    BigDecimal totalPriceWithoutVat;

    @Column(name = "total_vat_price", precision = 25, scale = 2)
    BigDecimal totalVatPrice;

    @Column(name = "total_brutto", precision = 25, scale = 6)
    BigDecimal totalBrutto;

    @Column(name = "total_netto", precision = 25, scale = 6)
    BigDecimal totalNetto;

    @Column(name = "total_qty_in_package")
    Integer totalQtyInPackage;

    @Column(name = "total_discount_price", precision = 25, scale = 2)
    BigDecimal totalDiscountPrice;

    @Column(name = "copy_order_id")
    Long copyOrderId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "copy_order_id", updatable = false, insertable = false)
    Order copyOrder;

    @Column(name = "order_type")
    @Enumerated(EnumType.STRING)
    OrderType orderType = OrderType.OUTGOING;

    @Column(name = "price_list_id")
    Long priceListId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "price_list_id", updatable = false, insertable = false)
    PriceList priceList;

    @Column(name = "stock_transfer_id")
    Long stockTransferId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "stock_transfer_id", updatable = false, insertable = false)
    StockTransfer stockTransfer;

    @Column(name = "trip_id")
    Long tripId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "trip_id", updatable = false, insertable = false)
    Trip trip;

    @Column(name = "clickhouse_sync", columnDefinition = "boolean default false")
    boolean clickhouseSync = false;

    @Column(name = "from_1c", columnDefinition = "boolean default false")
    boolean from1C = false;

    @Column(name = "from_hippo", columnDefinition = "boolean default false")
    boolean fromHippo = false;

    @Enumerated(EnumType.STRING)
    @Column(name = "app_type")
    AppType appType;

    @Enumerated(EnumType.STRING)
    @Column(name = "order_source")
    OrderSource orderSource = OrderSource.MANUAL;

    @Enumerated(EnumType.STRING)
    @Column(name = "delivery_type")
    DeliveryType deliveryType;

    @Column(name = "delivery_date")
    LocalDateTime deliveryDate;

    @Column(name = "specification_number")
    String specificationNumber;

    @Column(name = "specification_date")
    LocalDate specificationDate;

    @Column(name = "create_stock_in", columnDefinition = "boolean default false")
    boolean createStockIn = false;

    @Column(name = "expeditor_id")
    Long expeditorId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "expeditor_id", updatable = false, insertable = false)
    User expeditor;

    @Column(name = "agent_id")
    Long agentId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "agent_id", updatable = false, insertable = false)
    User agent;

    @Column(name = "plan_route_customer_branch_id")
    Long planRouteCustomerBranchId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "plan_route_customer_branch_id", insertable = false, updatable = false)
    PlanRouteCustomerBranch planRouteCustomerBranch;

    @Column(name = "plan_route_id")
    Long planRouteId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "plan_route_id", insertable = false, updatable = false)
    PlanRoute planRoute;

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true)
    @SQLRestriction("deleted = false")
    @OrderBy("sOrder,id")
    List<OrderProduct> products = new ArrayList<>();

    @Enumerated(EnumType.STRING)
    @Column(name = "reject_reason")
    OrderRejectReason rejectReason;

    public void addProduct(OrderProduct orderProduct) {
        orderProduct.setOrder(this);
        orderProduct.setOrderId(this.id);
        products.add(orderProduct);
    }

    @Override
    public String getName() {
        return this.getOrderNumber();
    }

    public OrderBaseDTO toOrderBaseDTO() {
        return new OrderBaseDTO(this.getId(), this.getName(), this.isFrom1C());
    }

    @Override
    public Long getEntityId() {
        return this.id;
    }

    @Override
    public EntityTypeEnum getEntityType() {
        return EntityTypeEnum.ORDER;
    }

    @Override
    public String getEntityName() {
        return getName();
    }

    public void setOrderNumber(String orderNumber) {
        addLogHistory("orderNumber", this.orderNumber, orderNumber);
        this.orderNumber = orderNumber;
    }

    public void setOrderDate(LocalDateTime orderDate) {
        addLogHistory("orderDate", this.orderDate, orderDate);
        this.orderDate = orderDate;
    }

    public void setContract(Contract contract) {
        addLogHistory("contract", this.contract != null ? this.contract.getName() : null, contract != null ? contract.getName() : null);
        this.contract = contract;
    }

    public void setBranch(Branch branch) {
        addLogHistory("branch", this.branch != null ? this.branch.getName() : null, branch != null ? branch.getName() : null);
        this.branch = branch;
    }

    public void setWarehouse(Warehouse warehouse) {
        addLogHistory("warehouse", this.warehouse != null ? this.warehouse.getName() : null, warehouse != null ? warehouse.getName() : null);
        this.warehouse = warehouse;
    }

    public void setStatus(OrderStatus status) {
        addLogHistory("status", this.status != null ? this.status.getName() : null, status != null ? status.getName() : null);
        this.status = status;
    }

    public void setSaleType(SaleType saleType) {
        addLogHistory("saleType", this.saleType != null ? this.saleType.getName() : null, saleType != null ? saleType.getName() : null);
        this.saleType = saleType;
    }

    public void setAdvancePaymentAmount(BigDecimal advancePaymentAmount) {
        addLogHistory("advancePaymentAmount", this.advancePaymentAmount, advancePaymentAmount);
        this.advancePaymentAmount = advancePaymentAmount;
    }

    public void setAdvancePaymentPercent(BigDecimal advancePaymentPercent) {
        addLogHistory("advancePaymentPercent", this.advancePaymentPercent, advancePaymentPercent);
        this.advancePaymentPercent = advancePaymentPercent;
    }

    public void setPaymentType(PaymentType paymentType) {
        addLogHistory("paymentType", this.paymentType != null ? this.paymentType.getName() : null, paymentType != null ? paymentType.getName() : null);
        this.paymentType = paymentType;
    }

    public void setDescription(String description) {
        addLogHistory("description", this.description, description);
        this.description = description;
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


    public void setOrderType(OrderType orderType) {
        addLogHistory("orderType", this.orderType != null ? this.orderType.getName() : null, orderType != null ? orderType.getName() : null);
        this.orderType = orderType;
    }

    public void setPriceList(PriceList priceList) {
        addLogHistory("priceList", this.priceList != null ? this.priceList.getName() : null, priceList != null ? priceList.getName() : null);
        this.priceList = priceList;
    }

    public void setClickhouseSync(boolean clickhouseSync) {
        addLogHistory("clickhouseSync", this.clickhouseSync, clickhouseSync);
        this.clickhouseSync = clickhouseSync;
    }

    public void setDeliveryAddress(Address address) {
        addAddressLogHistory(this.deliveryAddress, address);
        this.deliveryAddress = address;
    }
}
