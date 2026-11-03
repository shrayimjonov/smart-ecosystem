package uz.uzkassa.smartpossupply.domain.stocktransfer;

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
import uz.uzkassa.smartpossupply.domain.product.Product;
import uz.uzkassa.smartpossupply.domain.Unit;
import uz.uzkassa.smartpossupply.domain.base.AbstractAuditingEntity;
import uz.uzkassa.smartpossupply.domain.order.Order;
import uz.uzkassa.smartpossupply.domain.productserial.ProductSerial;
import uz.uzkassa.smartpossupply.enums.EntityTypeEnum;
import uz.uzkassa.smartpossupply.interfaces.Auditable;

import java.io.Serial;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
@Entity
@Table(name = "stock_transfer_product", indexes = {
    @Index(columnList = "stock_transfer_id", name = "stock_transfer_product_stock_transfer_id_idx"),
    @Index(columnList = "deleted", name = "stock_transfer_product_deleted_idx")
})
@SQLDelete(sql = "UPDATE stock_transfer_product SET deleted = true WHERE id=?")
public class StockTransferProduct extends AbstractAuditingEntity implements Auditable {

    @Serial
    private static final long serialVersionUID = 1329094756L;

    @Column(name = "from_product_id")
    Long fromProductId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "from_product_id", updatable = false, insertable = false)
    Product fromProduct;

    @Column(name = "to_product_id")
    Long toProductId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "to_product_id", updatable = false, insertable = false)
    Product toProduct;

    @Column(name = "unit_id")
    Long unitId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "unit_id", updatable = false, insertable = false)
    Unit unit;

    @Column(name = "order_id")
    Long orderId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id", updatable = false, insertable = false)
    Order order;

    @Column(name = "qty", precision = 25, scale = 2)
    BigDecimal qty;

    @Column(name = "price", precision = 25, scale = 2)
    BigDecimal price;

    @Column(name = "total_price", precision = 25, scale = 2)
    BigDecimal totalPrice;

    @Column(name = "scanned_qty", precision = 25, scale = 2)
    BigDecimal scannedQty;

    @Column(name = "total_brutto", precision = 25, scale = 6)
    BigDecimal totalBrutto;

    @Column(name = "total_netto", precision = 25, scale = 6)
    BigDecimal totalNetto;

    @Column(name = "block_qty")
    BigDecimal blockQty;

    @Column(name = "stock_transfer_id")
    Long stockTransferId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "stock_transfer_id", updatable = false, insertable = false)
    StockTransfer stockTransfer;

    @Column(name = "product_serial_id")
    Long productSerialId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_serial_id", updatable = false, insertable = false)
    ProductSerial productSerial;

    @Column(name = "has_marking", columnDefinition = "boolean default false")
    boolean hasMarking;

    @Column(name = "aggregation_qty", columnDefinition = "int4 default 0")
    Integer aggregationQty = 0;

    @Column(name = "mark_qty", columnDefinition = "int4 default 0")
    Integer markQty = 0;

    @Column(name = "aggregation_block_qty", columnDefinition = "int4 default 0")
    Integer aggregationBlockQty = 0;

    @OneToMany(mappedBy = "stockTransferProduct", cascade = CascadeType.ALL, orphanRemoval = true)
    @SQLRestriction("deleted = false")
    List<StockTransferProductAggregation> stockTransferProductAggregations = new ArrayList<>();

    @OneToMany(mappedBy = "stockTransferProduct", cascade = CascadeType.ALL, orphanRemoval = true)
    @SQLRestriction("deleted = false")
    List<StockTransferProductMark> stockTransferProductMarks = new ArrayList<>();

    @OneToMany(mappedBy = "stockTransferProduct", cascade = CascadeType.ALL, orphanRemoval = true)
    @SQLRestriction("deleted = false")
    List<StockTransferProductAggregationBlock> stockTransferProductAggregationBlocks = new ArrayList<>();

    @Override
    public Long getEntityId() {
        return this.id;
    }

    @Override
    public EntityTypeEnum getEntityType() {
        return EntityTypeEnum.STOCK_TRANSFER_PRODUCT;
    }

    @Override
    public String getEntityName() {
        return getFromProduct() != null ? getFromProduct().getName() : null;
    }
}
