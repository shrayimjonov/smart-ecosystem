package uz.uzkassa.smartpossupply.domain.stock;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;
import uz.uzkassa.smartpossupply.domain.EntityCompositable;
import uz.uzkassa.smartpossupply.domain.EntityCompositeId;
import uz.uzkassa.smartpossupply.domain.Lgota;
import uz.uzkassa.smartpossupply.domain.Unit;
import uz.uzkassa.smartpossupply.domain.base.AbstractAuditingEntity;
import uz.uzkassa.smartpossupply.domain.company.CompanyCommittent;
import uz.uzkassa.smartpossupply.domain.discount.Discount;
import uz.uzkassa.smartpossupply.domain.discount.DiscountItemProduct;
import uz.uzkassa.smartpossupply.domain.product.Product;
import uz.uzkassa.smartpossupply.domain.product.ProductContract;
import uz.uzkassa.smartpossupply.domain.productserial.ProductSerial;
import uz.uzkassa.smartpossupply.domain.warehouse.Warehouse;
import uz.uzkassa.smartpossupply.enums.EntityTypeEnum;
import uz.uzkassa.smartpossupply.interfaces.Auditable;

import java.io.Serial;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
@Entity
@Table(name = "stock_product", indexes = {
    @Index(columnList = "product_id", name = "stock_product_product_id_idx"),
    @Index(columnList = "stock_id", name = "stock_product_stock_id_idx"),
    @Index(columnList = "deleted", name = "stock_product_deleted_idx"),
})
@SQLDelete(sql = "UPDATE stock_product SET deleted = true WHERE id=?")
public class StockProduct extends AbstractAuditingEntity implements EntityCompositable, Auditable {

    @Serial
    private static final long serialVersionUID = 9094756L;

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

    @Column(name = "name", length = 2000)
    String name;

    @Column(name = "unit_id")
    Long unitId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "unit_id", updatable = false, insertable = false)
    Unit unit;

    @Column(name = "product_serial_id")
    Long productSerialId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_serial_id", updatable = false, insertable = false)
    ProductSerial productSerial;

    @Column(name = "requested_qty", precision = 25, scale = 2)
    BigDecimal requestedQty;

    @Column(name = "qty", precision = 25, scale = 2)
    BigDecimal qty;

    @Column(name = "turon_qty", precision = 25, scale = 2)
    BigDecimal turonQty;

    @Column(name = "qty_in_package", precision = 25, scale = 2)
    BigDecimal qtyInPackage;

    @Column(name = "qty_in_box", precision = 25, scale = 2)
    BigDecimal qtyInBox;

    @Column(name = "qty_in_pallet", precision = 25, scale = 2)
    BigDecimal qtyInPallet;

    @Column(name = "total_brutto", precision = 25, scale = 6)
    BigDecimal totalBrutto;

    @Column(name = "total_netto", precision = 25, scale = 6)
    BigDecimal totalNetto;

    @Column(name = "aggregation_qty", columnDefinition = "int4 default 0")
    Integer aggregationQty = 0;

    @Column(name = "mark_qty", columnDefinition = "int4 default 0")
    Integer markQty = 0;

    @Column(name = "aggregation_block_qty", columnDefinition = "int4 default 0")
    Integer aggregationBlockQty = 0;

    @Column(name = "selling_price", precision = 25, scale = 2)
    BigDecimal sellingPrice;

    @Column(name = "selling_foreign_price", precision = 25, scale = 2)
    BigDecimal sellingForeignPrice;

    @Column(name = "selling_price_without_vat", precision = 25, scale = 2)
    BigDecimal sellingPriceWithoutVat;

    @Column(name = "total_selling_price", precision = 25, scale = 2)
    BigDecimal totalSellingPrice;

    @Column(name = "total_selling_foreign_price", precision = 25, scale = 2)
    BigDecimal totalSellingForeignPrice;

    @Column(name = "total_selling_price_without_vat", precision = 25, scale = 2)
    BigDecimal totalSellingPriceWithoutVat;

    @Column(name = "purchase_price", precision = 25, scale = 2)
    BigDecimal purchasePrice;

    @Column(name = "purchase_foreign_price", precision = 25, scale = 2)
    BigDecimal purchaseForeignPrice;

    @Column(name = "purchase_price_without_vat", precision = 25, scale = 2)
    BigDecimal purchasePriceWithoutVat;

    @Column(name = "total_purchase_price", precision = 25, scale = 2)
    BigDecimal totalPurchasePrice;

    @Column(name = "total_purchase_foreign_price", precision = 25, scale = 2)
    BigDecimal totalPurchaseForeignPrice;

    @Column(name = "total_purchase_price_without_vat", precision = 25, scale = 2)
    BigDecimal totalPurchasePriceWithoutVat;

    @Column(name = "vat_rate", precision = 25, scale = 2)
    BigDecimal vatRate;

    @Column(name = "vat_price", precision = 25, scale = 2)
    BigDecimal vatPrice;

    @Column(name = "total_vat_price", precision = 25, scale = 2)
    BigDecimal totalVatPrice;

    @Column(name = "markup_percent", precision = 25, scale = 2)
    BigDecimal markupPercent;

    @Column(name = "markup_price", precision = 25, scale = 2)
    BigDecimal markupPrice;

    @Column(name = "discount_percent", precision = 25, scale = 2)
    BigDecimal discountPercent;

    @Column(name = "discount_price", precision = 25, scale = 2)
    BigDecimal discountPrice;

    @Column(name = "total_discount_price", precision = 25, scale = 2)
    BigDecimal totalDiscountPrice;

    @Column(name = "factura_profit_rate", precision = 25, scale = 2)
    BigDecimal facturaProfitRate;

    @Column(name = "price_list_price", precision = 25, scale = 2)
    BigDecimal priceListPrice;

    @Column(name = "base_price", precision = 25, scale = 2)
    BigDecimal basePrice;

    @Column(name = "excise_rate", precision = 25, scale = 2)
    BigDecimal exciseRate = BigDecimal.ZERO;

    @Column(name = "excise_sum", precision = 25, scale = 2)
    BigDecimal exciseSum = BigDecimal.ZERO;

    @Column(name = "has_marking", columnDefinition = "boolean default false")
    boolean hasMarking;

    @Column(name = "clickhouse_sync", columnDefinition = "boolean default false")
    boolean clickhouseSync = false;

    @Column(name = "expiry_date")
    LocalDate expiryDate;

    @Column(name = "s_order")
    Integer sOrder;

    @Column(name = "code_1c")
    String code1C;

    @Column(name = "package_name", length = 1000)
    String packageName;

    @Column(name = "package_code", length = 1000)
    String packageCode;

    @Column(name = "discount_product_unique_id")
    String discountProductUniqueId;

    @Column(name = "parent_discount_product_unique_id")
    String parentDiscountProductUniqueId;

    @Column(name = "company_committent_id")
    Long companyCommittentId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "company_committent_id", updatable = false, insertable = false)
    CompanyCommittent companyCommittent;

    @Column(name = "lgota_id")
    Long lgotaId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "lgota_id", updatable = false, insertable = false)
    Lgota lgota;

    @Column(name = "lgota_vat_sum", precision = 25, scale = 2)
    BigDecimal lgotaVatSum;

    @Column(name = "stock_id")
    Long stockId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "stock_id", updatable = false, insertable = false)
    Stock stock;

    @Column(name = "product_contract_id")
    Long productContractId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_contract_id", updatable = false, insertable = false)
    ProductContract productContract;

    @OneToMany(mappedBy = "stockProduct", cascade = CascadeType.ALL, orphanRemoval = true)
    @SQLRestriction("deleted = false")
    List<StockProductAggregationBlock> stockProductAggregationBlocks = new ArrayList<>();

    @OneToMany(mappedBy = "stockProduct", cascade = CascadeType.ALL, orphanRemoval = true)
    @SQLRestriction("deleted = false")
    List<StockProductAggregation> stockProductAggregations = new ArrayList<>();

    @OneToMany(mappedBy = "stockProduct", cascade = CascadeType.ALL, orphanRemoval = true)
    @SQLRestriction("deleted = false")
    List<StockProductMark> stockProductMarks = new ArrayList<>();

    @Column(name = "checked", columnDefinition = "boolean default false")
    boolean checked = false;

    @Column(name = "has_warning", columnDefinition = "boolean default false")
    boolean hasWarning = false;

    @Column(name = "scanned_qty", precision = 25, scale = 2)
    BigDecimal scannedQty;

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
        return new EntityCompositeId(this.getId(), EntityTypeEnum.STOCK_PRODUCT);
    }

    public void addStockProductAggregationBlock(StockProductAggregationBlock stockProductAggregationBlock) {
        stockProductAggregationBlock.setStockProduct(this);
        stockProductAggregationBlock.setStockProductId(this.id);
        stockProductAggregationBlocks.add(stockProductAggregationBlock);
    }

    public void addStockProductAggregation(StockProductAggregation stockProductAggregation) {
        stockProductAggregation.setStockProduct(this);
        stockProductAggregation.setStockProductId(this.id);
        stockProductAggregations.add(stockProductAggregation);
    }

    public void addStockProductMark(StockProductMark stockProductMark) {
        stockProductMark.setStockProduct(this);
        stockProductMark.setStockProductId(this.id);
        stockProductMarks.add(stockProductMark);
    }

    @Override
    public Long getEntityId() {
        return this.id;
    }

    @Override
    public EntityTypeEnum getEntityType() {
        return EntityTypeEnum.STOCK_PRODUCT;
    }

    @Override
    public String getEntityName() {
        return getName();
    }
}
