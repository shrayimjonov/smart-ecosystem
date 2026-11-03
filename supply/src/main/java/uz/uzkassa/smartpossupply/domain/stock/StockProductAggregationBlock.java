package uz.uzkassa.smartpossupply.domain.stock;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import org.apache.commons.lang3.StringUtils;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.type.SqlTypes;
import uz.uzkassa.smartpossupply.domain.product.Product;
import uz.uzkassa.smartpossupply.domain.base.AbstractAuditingEntity;
import uz.uzkassa.smartpossupply.domain.company.CompanyCommittent;
import uz.uzkassa.smartpossupply.dto.company.CompanyBaseDTO;
import uz.uzkassa.smartpossupply.dto.stock.CheckedCodeDTO;
import uz.uzkassa.smartpossupply.enums.EntityTypeEnum;
import uz.uzkassa.smartpossupply.enums.PackageType;
import uz.uzkassa.smartpossupply.enums.TransferType;
import uz.uzkassa.smartpossupply.enums.TuronMarkStatus;
import uz.uzkassa.smartpossupply.interfaces.Auditable;

import java.io.Serial;
import java.math.BigDecimal;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
@Entity
@SQLDelete(sql = "UPDATE stock_product_aggregation_block SET deleted = true WHERE id=?")
@Table(name = "stock_product_aggregation_block", indexes = {
        @Index(name = "stock_product_aggregation_block_stock_product_id_idx", columnList = "stock_product_id"),
        @Index(name = "stock_product_aggregation_block_code_idx", columnList = "code"),
        @Index(name = "stock_product_aggregation_block_print_code_idx", columnList = "print_code"),
        @Index(name = "stock_product_aggregation_block_transfer_type_idx", columnList = "transfer_type"),
        @Index(name = "stock_product_aggregation_block_product_id_idx", columnList = "product_id"),
        @Index(name = "stock_product_aggregation_block_deleted_idx", columnList = "deleted"),
        @Index(name = "stock_product_aggregation_block_code_transfer_type_deleted_idx", columnList = "code, transfer_type, deleted"),
})
public class StockProductAggregationBlock extends AbstractAuditingEntity implements Auditable {

    @Serial
    private static final long serialVersionUID = 1090423492274346L;

    @Column(name = "stock_product_id")
    Long stockProductId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "stock_product_id", updatable = false, insertable = false)
    StockProduct stockProduct;

    @Column(name = "code")
    String code;

    @Column(name = "print_code")
    String originalCode;

    @Column(name = "qty", precision = 25, scale = 2)
    BigDecimal qty;

    @Column(name = "parent_code")
    String parentCode;

    @Column(name = "product_id")
    Long productId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", updatable = false, insertable = false)
    Product product;

    @Enumerated(EnumType.STRING)
    @Column(name = "transfer_type")
    TransferType transferType;

    @Column(name = "company_committent_id")
    Long companyCommittentId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "company_committent_id", updatable = false, insertable = false)
    CompanyCommittent companyCommittent;

    @Column(name = "checked", columnDefinition = "boolean default false")
    boolean checked = false;

    @Column(name = "checked_in_stock", columnDefinition = "boolean default false")
    boolean checkedInStock = false;

    @Column(name = "clickhouse_sync", columnDefinition = "boolean default false")
    boolean clickhouseSync = false;

    @Column(name = "clickhouse_history_sync", columnDefinition = "boolean default false")
    boolean clickhouseHistorySync = false;

    @Column(name = "used", columnDefinition = "boolean default true")
    boolean used = true;

    @Column(name = "parent", columnDefinition = "boolean default true")
    boolean parent = true;

    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    TuronMarkStatus status;

    @Enumerated(EnumType.STRING)
    @Column(name = "package_type")
    PackageType packageType;

    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.PERSIST)
    @JoinColumn(name = "box_id")
    StockProductAggregation box;

    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.PERSIST)
    @JoinColumn(name = "pallet_id")
    StockProductAggregation pallet;

    @Column(name = "owner_name")
    String ownerName;

    @Column(name = "owner_tin")
    String ownerTin;

    @JdbcTypeCode(SqlTypes.LONGVARCHAR)
    @Column(name = "comment")
    String comment;

    public CheckedCodeDTO toCheckDTO() {
        return new CheckedCodeDTO(getCode() != null ? getCode() : getOriginalCode(), getOriginalCode() != null ? getOriginalCode() : getCode(), null, isChecked(), getQty(), getOwner());
    }

    public CompanyBaseDTO getOwner() {
        if (StringUtils.isNotEmpty(this.getOwnerTin())) {
            return new CompanyBaseDTO(null, this.getOwnerName(), this.getOwnerTin());
        }
        return null;
    }

    @Override
    public Long getEntityId() {
        return this.id;
    }

    @Override
    public EntityTypeEnum getEntityType() {
        return EntityTypeEnum.STOCK_PRODUCT_AGGREGATION_BLOCK;
    }

    @Override
    public String getEntityName() {
        return getCode();
    }
}
