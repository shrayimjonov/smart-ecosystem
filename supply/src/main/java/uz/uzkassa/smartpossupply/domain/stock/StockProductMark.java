package uz.uzkassa.smartpossupply.domain.stock;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import org.apache.commons.lang3.StringUtils;
import org.hibernate.annotations.SQLDelete;
import uz.uzkassa.smartpossupply.domain.base.AbstractAuditingEntity;
import uz.uzkassa.smartpossupply.domain.company.CompanyCommittent;
import uz.uzkassa.smartpossupply.domain.product.Product;
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
@Table(name = "stock_product_mark", indexes = {
        @Index(name = "stock_product_mark_stock_product_id_idx", columnList = "stock_product_id"),
        @Index(name = "stock_product_mark_code_idx", columnList = "code"),
        @Index(name = "stock_product_mark_print_code_idx", columnList = "print_code"),
        @Index(name = "stock_product_mark_product_id_idx", columnList = "product_id"),
        @Index(name = "stock_product_mark_transfer_type_idx", columnList = "transfer_type"),
        @Index(name = "stock_product_mark_deleted_idx", columnList = "deleted"),
        @Index(name = "stock_product_mark_code_transfer_type_deleted_idx", columnList = "code, transfer_type, deleted")
})
@SQLDelete(sql = "UPDATE stock_product_mark SET deleted = true WHERE id=?")
public class StockProductMark extends AbstractAuditingEntity implements Auditable {

    @Serial
    private static final long serialVersionUID = 606043056L;

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

    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    TuronMarkStatus status;

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
    @Column(name = "package_type")
    PackageType packageType;

    @ManyToOne(cascade = CascadeType.PERSIST, fetch = FetchType.LAZY)
    @JoinColumn(name = "box_id")
    StockProductAggregation box;

    @ManyToOne(cascade = CascadeType.PERSIST, fetch = FetchType.LAZY)
    @JoinColumn(name = "block_id")
    StockProductAggregationBlock block;

    @ManyToOne(cascade = CascadeType.PERSIST, fetch = FetchType.LAZY)
    @JoinColumn(name = "pallet_id")
    StockProductAggregation pallet;

    @Column(name = "owner_name")
    String ownerName;

    @Column(name = "owner_tin")
    String ownerTin;

    @Column(name = "comment", columnDefinition = "TEXT")
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
        return EntityTypeEnum.STOCK_PRODUCT_MARK;
    }

    @Override
    public String getEntityName() {
        return getCode();
    }

}
