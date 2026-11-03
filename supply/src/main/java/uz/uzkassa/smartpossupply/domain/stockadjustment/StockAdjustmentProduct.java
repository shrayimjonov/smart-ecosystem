package uz.uzkassa.smartpossupply.domain.stockadjustment;

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
@Table(name = "stock_adjustment_product", indexes = {
        @Index(columnList = "stock_adjustment_id", name = "stock_adjustment_product_stock_adjustment_id_idx"),
        @Index(columnList = "deleted", name = "stock_adjustment_product_deleted_idx"),
})
@SQLDelete(sql = "UPDATE stock_adjustment_product SET deleted = true WHERE id=?")
public class StockAdjustmentProduct extends AbstractAuditingEntity implements Auditable {

    @Serial
    private static final long serialVersionUID = 976541329094756L;

    @Column(name = "product_id")
    Long productId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", updatable = false, insertable = false)
    Product product;

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

    @Column(name = "has_marking", columnDefinition = "boolean default false")
    boolean hasMarking;

    @Column(name = "stock_adjustment_id")
    Long stockAdjustmentId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "stock_adjustment_id", updatable = false, insertable = false)
    StockAdjustment stockAdjustment;

    @Column(name = "product_serial_id")
    Long productSerialId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_serial_id", updatable = false, insertable = false)
    ProductSerial productSerial;

    @OneToMany(mappedBy = "stockAdjustmentProduct", cascade = CascadeType.ALL, orphanRemoval = true)
    @SQLRestriction("deleted = false")
    List<StockAdjustmentProductAggregation> stockAdjustmentProductAggregations = new ArrayList<>();

    @OneToMany(mappedBy = "stockAdjustmentProduct", cascade = CascadeType.ALL, orphanRemoval = true)
    @SQLRestriction("deleted = false")
    List<StockAdjustmentProductMark> stockAdjustmentProductMarks = new ArrayList<>();

    @OneToMany(mappedBy = "stockAdjustmentProduct", cascade = CascadeType.ALL, orphanRemoval = true)
    @SQLRestriction("deleted = false")
    List<StockAdjustmentProductAggregationBlock> stockAdjustmentProductAggregationBlocks = new ArrayList<>();

    @Override
    public Long getEntityId() {
        return this.id;
    }

    @Override
    public EntityTypeEnum getEntityType() {
        return EntityTypeEnum.STOCK_ADJUSTMENT_PRODUCT;
    }

    @Override
    public String getEntityName() {
        return getProduct() != null ? getProduct().getName() : null;
    }
}
