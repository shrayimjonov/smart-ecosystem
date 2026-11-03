package uz.uzkassa.smartpossupply.domain.stockadjustment;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import org.hibernate.annotations.SQLDelete;
import uz.uzkassa.smartpossupply.domain.product.Product;
import uz.uzkassa.smartpossupply.domain.base.AbstractAuditingEntity;
import uz.uzkassa.smartpossupply.dto.stock.CheckedCodeDTO;
import uz.uzkassa.smartpossupply.enums.EntityTypeEnum;
import uz.uzkassa.smartpossupply.interfaces.Auditable;

import java.io.Serial;
import java.math.BigDecimal;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
@Entity
@SQLDelete(sql = "UPDATE stock_adjustment_product_aggregation_block SET deleted = true WHERE id=?")
@Table(name = "stock_adjustment_product_aggregation_block", indexes = {
        @Index(columnList = "stock_adjustment_product_id", name = "stock_adjustment_product_aggregation_block_sap_id_idx"),
        @Index(columnList = "deleted", name = "stock_adjustment_product_aggregation_block_deleted_idx"),
})
public class StockAdjustmentProductAggregationBlock extends AbstractAuditingEntity implements Auditable {

    @Serial
    private static final long serialVersionUID = 244483983453L;

    @Column(name = "stock_adjustment_product_id")
    Long stockAdjustmentProductId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "stock_adjustment_product_id", updatable = false, insertable = false)
    StockAdjustmentProduct stockAdjustmentProduct;

    @Column(name = "code")
    String code;

    @Column(name = "original_code")
    String originalCode;

    @Column(name = "qty", precision = 25, scale = 2)
    BigDecimal qty;

    @Column(name = "product_id")
    Long productId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", updatable = false, insertable = false)
    Product product;

    public CheckedCodeDTO toCheckDTO() {
        return new CheckedCodeDTO(getCode() != null ? getCode() : getOriginalCode(), getOriginalCode() != null ? getOriginalCode() : getCode(), null, false, null);
    }

    @Override
    public Long getEntityId() {
        return this.id;
    }

    @Override
    public EntityTypeEnum getEntityType() {
        return EntityTypeEnum.STOCK_ADJUSTMENT_PRODUCT_AGGREGATION_BLOCK;
    }

    @Override
    public String getEntityName() {
        return getCode();
    }
}
