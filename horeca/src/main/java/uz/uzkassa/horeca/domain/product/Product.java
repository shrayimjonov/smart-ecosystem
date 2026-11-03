package uz.uzkassa.horeca.domain.product;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.type.SqlTypes;
import uz.uzkassa.horeca.domain.Category;
import uz.uzkassa.horeca.domain.Department;
import uz.uzkassa.horeca.domain.FileUpload;
import uz.uzkassa.horeca.domain.base.AbstractAuditingEntity;
import uz.uzkassa.horeca.domain.branch.Branch;
import uz.uzkassa.horeca.domain.unit.Unit;
import uz.uzkassa.horeca.enums.*;

import java.io.Serial;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.UUID;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
@Table(name = "product",
        indexes = {
                @Index(columnList = "tasnif_id", name = "product_tasnif_id_idx"),
                @Index(columnList = "name", name = "product_name_idx"),
                @Index(columnList = "sku", name = "product_sku_idx"),
                @Index(columnList = "barcode", name = "product_barcode_idx"),
                @Index(columnList = "catalog_code", name = "product_catalog_code_idx"),
                @Index(columnList = "product_type", name = "product_product_type_idx"),
                @Index(columnList = "unit_id", name = "product_unit_id_idx"),
                @Index(columnList = "category_id", name = "product_category_id_idx"),
                @Index(columnList = "department_id", name = "product_department_id_idx"),
                @Index(columnList = "warehouse_id", name = "product_warehouse_id_idx"),
                @Index(columnList = "branch_id", name = "product_branch_id_idx")
        }
)
@Entity
@SQLDelete(sql = "UPDATE product SET deleted='true' WHERE id=?")
public class Product extends AbstractAuditingEntity implements Serializable {

    @Serial
    private static final long serialVersionUID = 67L;

    @NotNull
    @Column(name = "unique_id", nullable = false)
    private UUID uniqueId;

    @Column(name = "tasnif_id")
    String tasnifId;

    @NotNull
    @Column(name = "name", nullable = false)
    String name;

    @Column(name = "sku")
    String sku;

    @Column(name = "brand")
    String brand;

    @Column(name = "barcode")
    String barcode;

    @Column(name = "catalog_code")
    String catalogCode;

    @Column(name = "catalog_name")
    String catalogName;

    @Column(name = "package_name")
    String packageName;

    @Column(name = "package_code")
    String packageCode;

    @JdbcTypeCode(SqlTypes.LONGVARCHAR)
    @Column(name = "description")
    String description;

    @Column(name = "status")
    @Enumerated(value = EnumType.STRING)
    CommonStatus status = CommonStatus.IN_ACTIVE;

    @Column(name = "product_type")
    @Enumerated(value = EnumType.STRING)
    ProductType productType;

    @Column(name = "unit_id")
    Long unitId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "unit_id", insertable = false, updatable = false)
    Unit unit;

    @Column(name = "vat")
    @Enumerated(EnumType.STRING)
    Vat vat = Vat.VAT_12;

    @Column(name = "price", scale = 2, precision = 25)
    BigDecimal price;

    @Column(name = "weight", scale = 2, precision = 25)
    BigDecimal weight;

    @Column(name = "has_mark")
    boolean hasMark;

    @Column(name = "favourite", columnDefinition = "boolean default false")
    boolean favourite = false;

    @NotNull
    @Column(name = "category_id", nullable = false)
    Long categoryId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id", insertable = false, updatable = false)
    Category category;

    @Column(name = "photo_id")
    Long photoId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "photo_id", insertable = false, updatable = false)
    FileUpload photo;

    @Column(name = "department_id")
    Long departmentId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "department_id", insertable = false, updatable = false)
    Department department;

    @NotNull
    @Column(name = "branch_id", nullable = false)
    Long branchId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "branch_id", insertable = false, updatable = false)
    Branch branch;

    @Column(name = "origin")
    @Enumerated(EnumType.STRING)
    ProductOrigin origin;

    @Column(name = "has_tech_card", columnDefinition = "boolean default false")
    boolean hasTechCard = false;

    @Column(name = "menu_included", columnDefinition = "boolean default false")
    boolean menuIncluded = false;

    public boolean hasVat() {
        if (getVat() == null) return false;
        if (getVat().getAmount() == null) return false;
        return BigDecimal.ZERO.compareTo(getVat().getAmount()) != 0;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Product)) {
            return false;
        }
        return id != null && id.equals(((Product) o).id);
    }

    @Override
    public Long getEntityId() {
        return id;
    }

    @Override
    public EntityTypeEnum getEntityType() {
        return EntityTypeEnum.PRODUCT;
    }
}
