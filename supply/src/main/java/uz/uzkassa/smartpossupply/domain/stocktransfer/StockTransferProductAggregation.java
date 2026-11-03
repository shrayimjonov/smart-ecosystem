package uz.uzkassa.smartpossupply.domain.stocktransfer;

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
import uz.uzkassa.smartpossupply.enums.PackageType;
import uz.uzkassa.smartpossupply.enums.TuronMarkStatus;
import uz.uzkassa.smartpossupply.interfaces.Auditable;

import java.io.Serial;
import java.math.BigDecimal;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
@Entity
@SQLDelete(sql = "UPDATE stock_transfer_product_aggregation SET deleted = true WHERE id=?")
@Table(name = "stock_transfer_product_aggregation", indexes = {
        @Index(columnList = "stock_transfer_product_id", name = "stock_transfer_product_aggregation_stp_id_idx"),
        @Index(columnList = "deleted", name = "stock_transfer_product_aggregation_deleted_idx"),
})
public class StockTransferProductAggregation extends AbstractAuditingEntity implements Auditable {

    @Serial
    private static final long serialVersionUID = 34210904974004L;

    @Column(name = "stock_transfer_product_id")
    Long stockTransferProductId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "stock_transfer_product_id", updatable = false, insertable = false)
    StockTransferProduct stockTransferProduct;

    @Column(name = "code")
    String code;

    @Column(name = "original_code")
    String originalCode;

    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    TuronMarkStatus status;

    @Enumerated(EnumType.STRING)
    @Column(name = "package_type")
    PackageType packageType;

    @Column(name = "owner_name")
    String ownerName;

    @Column(name = "owner_tin")
    String ownerTin;

    @Column(name = "qty", precision = 25, scale = 2)
    BigDecimal qty;

    @Column(name = "product_id")
    Long productId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", updatable = false, insertable = false)
    Product product;

    @Override
    public Long getEntityId() {
        return this.id;
    }

    @Override
    public EntityTypeEnum getEntityType() {
        return EntityTypeEnum.STOCK_TRANSFER_PRODUCT_AGGREGATION;
    }

    @Override
    public String getEntityName() {
        return getCode();
    }

    public CheckedCodeDTO toCheckDTO() {
        return new CheckedCodeDTO(getCode() != null ? getCode() : getOriginalCode(), getOriginalCode() != null ? getOriginalCode() : getCode(), getQty());
    }
}
