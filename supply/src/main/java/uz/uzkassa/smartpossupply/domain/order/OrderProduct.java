package uz.uzkassa.smartpossupply.domain.order;

import jakarta.persistence.Column;
import jakarta.persistence.ColumnResult;
import jakarta.persistence.ConstructorResult;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.SqlResultSetMapping;
import jakarta.persistence.SqlResultSetMappings;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import org.hibernate.annotations.SQLDelete;
import uz.uzkassa.smartpossupply.domain.EntityCompositable;
import uz.uzkassa.smartpossupply.domain.EntityCompositeId;
import uz.uzkassa.smartpossupply.domain.Lgota;
import uz.uzkassa.smartpossupply.domain.Unit;
import uz.uzkassa.smartpossupply.domain.base.BaseEntity;
import uz.uzkassa.smartpossupply.domain.company.CompanyCommittent;
import uz.uzkassa.smartpossupply.domain.discount.Discount;
import uz.uzkassa.smartpossupply.domain.discount.DiscountItemProduct;
import uz.uzkassa.smartpossupply.domain.product.Product;
import uz.uzkassa.smartpossupply.domain.product.ProductContract;
import uz.uzkassa.smartpossupply.domain.productserial.ProductSerial;
import uz.uzkassa.smartpossupply.domain.warehouse.Warehouse;
import uz.uzkassa.smartpossupply.dto.order.OrderProductQtyDetailListDTO;
import uz.uzkassa.smartpossupply.dto.order.SalesAndCustomerReportDTO;
import uz.uzkassa.smartpossupply.dto.order.SalesAndCustomerSummaryDTO;
import uz.uzkassa.smartpossupply.enums.EntityTypeEnum;

import java.io.Serial;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
@Entity
@Table(name = "order_product",
        indexes = {
                @Index(columnList = "product_id", name = "order_product_product_id_idx"),
                @Index(columnList = "order_id", name = "order_product_order_id_idx"),
        })
@SqlResultSetMappings({
        @SqlResultSetMapping(name = "OrderProductQtyDetailListMapper",
                classes = {
                        @ConstructorResult(targetClass = OrderProductQtyDetailListDTO.class,
                                columns = {
                                        @ColumnResult(name = "id", type = Long.class),
                                        @ColumnResult(name = "orderNumber", type = String.class),
                                        @ColumnResult(name = "orderDate", type = LocalDateTime.class),
                                        @ColumnResult(name = "orderStatus", type = String.class),
                                        @ColumnResult(name = "qty", type = BigDecimal.class),
                                        @ColumnResult(name = "price", type = BigDecimal.class),
                                        @ColumnResult(name = "priceWithoutVat", type = BigDecimal.class),
                                        @ColumnResult(name = "vatPrice", type = BigDecimal.class),
                                        @ColumnResult(name = "totalPriceWithoutVat", type = BigDecimal.class),
                                        @ColumnResult(name = "totalPrice", type = BigDecimal.class),
                                        @ColumnResult(name = "companyId", type = Long.class),
                                        @ColumnResult(name = "companyName", type = String.class),
                                        @ColumnResult(name = "companyTin", type = String.class),
                                        @ColumnResult(name = "companyBrand", type = String.class),
                                        @ColumnResult(name = "agentId", type = Long.class),
                                        @ColumnResult(name = "agentFirstName", type = String.class),
                                        @ColumnResult(name = "agentLastName", type = String.class),
                                        @ColumnResult(name = "lastModifiedDate", type = LocalDateTime.class)
                                })
                }),
        @SqlResultSetMapping(name = "SalesAndCustomerSummaryMapper",
                classes = {
                        @ConstructorResult(targetClass = SalesAndCustomerSummaryDTO.class,
                                columns = {
                                        @ColumnResult(name = "totalProductsCount", type = Long.class),
                                        @ColumnResult(name = "totalQuantity", type = BigDecimal.class),
                                        @ColumnResult(name = "totalAmountWithoutVat", type = BigDecimal.class),
                                        @ColumnResult(name = "totalVatAmount", type = BigDecimal.class),
                                        @ColumnResult(name = "totalAmountWithVat", type = BigDecimal.class)
                                })
                }),
        @SqlResultSetMapping(name = "SalesAndCustomerReportMapper",
                classes = {
                        @ConstructorResult(targetClass = SalesAndCustomerReportDTO.class,
                                columns = {
                                        @ColumnResult(name = "categoryName", type = String.class),
                                        @ColumnResult(name = "customerName", type = String.class),
                                        @ColumnResult(name = "customerTin", type = String.class),
                                        @ColumnResult(name = "customerBranch", type = String.class),
                                        @ColumnResult(name = "productName", type = String.class),
                                        @ColumnResult(name = "barcode", type = String.class),
                                        @ColumnResult(name = "contractNumber", type = String.class),
                                        @ColumnResult(name = "contractPrice", type = BigDecimal.class),
                                        @ColumnResult(name = "quantity", type = BigDecimal.class),
                                        @ColumnResult(name = "serialNumber", type = String.class),
                                        @ColumnResult(name = "basePrice", type = BigDecimal.class),
                                        @ColumnResult(name = "markupPercent", type = BigDecimal.class),
                                        @ColumnResult(name = "markupAmount", type = BigDecimal.class),
                                        @ColumnResult(name = "priceWithoutVat", type = BigDecimal.class),
                                        @ColumnResult(name = "vatPrice", type = BigDecimal.class),
                                        @ColumnResult(name = "priceWithVat", type = BigDecimal.class),
                                        @ColumnResult(name = "branch", type = String.class),
                                        @ColumnResult(name = "warehouse", type = String.class)

                                })
                }),
})
@SQLDelete(sql = "UPDATE order_product SET deleted = true WHERE id=?")
public class OrderProduct extends BaseEntity implements EntityCompositable {

    @Serial
    private static final long serialVersionUID = 109089460323L;

    @Column(name = "product_id")
    Long productId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", updatable = false, insertable = false)
    Product product;

    @Column(name = "warehouse_id")
    Long warehouseId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "warehouse_id", updatable = false, insertable = false)
    Warehouse warehouse;

    @Column(name = "unit_id")
    Long unitId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "unit_id", updatable = false, insertable = false)
    Unit unit;

    @Column(name = "qty", precision = 25, scale = 2)
    BigDecimal qty;

    @Column(name = "price", precision = 25, scale = 2)
    BigDecimal price;

    @Column(name = "foreign_price", precision = 25, scale = 2)
    BigDecimal foreignPrice;

    @Column(name = "price_without_vat", precision = 25, scale = 2)
    BigDecimal priceWithoutVat;

    @Column(name = "total_price", precision = 25, scale = 2)
    BigDecimal totalPrice;

    @Column(name = "total_foreign_price", precision = 25, scale = 2)
    BigDecimal totalForeignPrice;

    @Column(name = "total_price_without_vat", precision = 25, scale = 2)
    BigDecimal totalPriceWithoutVat;

    @Column(name = "vat_rate", precision = 25, scale = 2)
    BigDecimal vatRate;

    @Column(name = "vat_price", precision = 25, scale = 2)
    BigDecimal vatPrice;

    @Column(name = "total_vat_price", precision = 25, scale = 2)
    BigDecimal totalVatPrice;

    @Column(name = "price_list_price", precision = 25, scale = 2)
    BigDecimal priceListPrice;

    @Column(name = "markup_percent", precision = 25, scale = 2)
    BigDecimal markupPercent;

    @Column(name = "markup_price", precision = 25, scale = 2)
    BigDecimal markupPrice;

    @Column(name = "total_brutto", precision = 25, scale = 6)
    BigDecimal totalBrutto;

    @Column(name = "total_netto", precision = 25, scale = 6)
    BigDecimal totalNetto;

    @Column(name = "total_qty_in_package")
    Integer totalQtyInPackage;

    @Column(name = "discount_percent", precision = 25, scale = 2)
    BigDecimal discountPercent;

    @Column(name = "discount_price", precision = 25, scale = 2)
    BigDecimal discountPrice;

    @Column(name = "total_discount_price", precision = 25, scale = 2)
    BigDecimal totalDiscountPrice;

    @Column(name = "expiry_date")
    LocalDate expiryDate;

    @Column(name = "code_1c")
    String code1C;

    @Column(name = "base_price", precision = 25, scale = 2)
    BigDecimal basePrice;

    @Column(name = "excise_rate", precision = 25, scale = 2)
    BigDecimal exciseRate = BigDecimal.ZERO;

    @Column(name = "excise_sum", precision = 25, scale = 2)
    BigDecimal exciseSum = BigDecimal.ZERO;

    @Column(name = "package_name", length = 1000)
    String packageName;

    @Column(name = "package_code", length = 1000)
    String packageCode;

    @Column(name = "product_serial_id")
    Long productSerialId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_serial_id", updatable = false, insertable = false)
    ProductSerial productSerial;

    @Column(name = "company_committent_id")
    Long companyCommittentId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "company_committent_id", updatable = false, insertable = false)
    CompanyCommittent companyCommittent;

    @Column(name = "committent_vat_reg_code")
    String committentVatRegCode;

    @Column(name = "committent_vat_reg_status")
    Integer committentVatRegStatus;

    @Column(name = "s_order")
    Integer sOrder;

    @Column(name = "discount_product_unique_id")
    String discountProductUniqueId;

    @Column(name = "parent_discount_product_unique_id")
    String parentDiscountProductUniqueId;

    @Column(name = "lgota_id")
    Long lgotaId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "lgota_id", updatable = false, insertable = false)
    Lgota lgota;

    @Column(name = "lgota_vat_sum", precision = 25, scale = 2)
    BigDecimal lgotaVatSum;

    @Column(name = "order_id")
    Long orderId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id", updatable = false, insertable = false)
    Order order;

    @Column(name = "product_contract_id")
    Long productContractId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_contract_id", updatable = false, insertable = false)
    ProductContract productContract;

    @Column(name = "discount_id")
    Long discountId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "discount_id", updatable = false, insertable = false)
    Discount discount;

    @Column(name = "discount_item_product_id")
    Long discountItemProductId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "discount_item_product_id", updatable = false, insertable = false)
    DiscountItemProduct discountItemProduct;

    @Override
    public EntityCompositeId getCompositeId() {
        return new EntityCompositeId(this.getId(), EntityTypeEnum.ORDER_PRODUCT);
    }
}
