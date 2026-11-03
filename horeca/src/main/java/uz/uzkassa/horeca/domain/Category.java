package uz.uzkassa.horeca.domain;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;
import org.hibernate.type.SqlTypes;
import uz.uzkassa.horeca.domain.base.BaseEntity;
import uz.uzkassa.horeca.domain.branch.Branch;
import uz.uzkassa.horeca.dto.category.CatalogDTO;
import uz.uzkassa.horeca.dto.category.CategoryLookUpDTO;
import uz.uzkassa.horeca.dto.category.CategoryTreeDTO;
import uz.uzkassa.horeca.enums.CategoryType;
import uz.uzkassa.horeca.enums.CommonStatus;
import uz.uzkassa.horeca.enums.ProductOrigin;
import uz.uzkassa.horeca.enums.Vat;

import java.io.Serial;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;


@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
@Entity
@Table(name = "category",
        indexes = {
                @Index(columnList = "name", name = "category_name_idx"),
                @Index(columnList = "catalog_code", name = "category_catalog_code_idx"),
                @Index(columnList = "catalog_name", name = "category_catalog_name_idx"),
                @Index(columnList = "barcode", name = "category_barcode_idx"),
                @Index(columnList = "package_code", name = "category_package_code_idx"),
                @Index(columnList = "package_name", name = "category_package_name_idx"),
                @Index(columnList = "branch_id", name = "category_branch_id_idx"),
                @Index(columnList = "category_type", name = "category_category_type_idx"),
                @Index(columnList = "parent_id", name = "category_parent_id_idx")
        })
@SQLDelete(sql = "UPDATE category SET deleted='true' WHERE id=?")
public class Category extends BaseEntity implements Serializable {

    @Serial
    private static final long serialVersionUID = 4367L;

    @NotNull
    @Column(name = "unique_id", nullable = false)
    private UUID uniqueId;

    @NotNull
    @Column(name = "name", nullable = false)
    String name;

    @Column(name = "catalog_code")
    String catalogCode;

    @JdbcTypeCode(SqlTypes.LONGVARCHAR)
    @Column(name = "catalog_name")
    String catalogName;

    @Column(name = "barcode")
    String barcode;

    @Column(name = "package_code")
    String packageCode;

    @Column(name = "package_name")
    String packageName;

    @Column(name = "vat")
    @Enumerated(EnumType.STRING)
    Vat vat = Vat.VAT_12;

    @Column(name = "origin")
    @Enumerated(EnumType.STRING)
    ProductOrigin origin;

    @Column(name = "branch_id")
    Long branchId;

    @ManyToOne(cascade = CascadeType.PERSIST, fetch = FetchType.LAZY)
    @JoinColumn(name = "branch_id", insertable = false, updatable = false)
    Branch branch;

    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    CommonStatus status = CommonStatus.ACTIVE;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "category_type", nullable = false)
    CategoryType categoryType;

    @Column(name = "parent_id")
    Long parentId;

    @ManyToOne(cascade = CascadeType.PERSIST, fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_id", updatable = false, insertable = false)
    Category parent;

    @OneToMany(mappedBy = "parent", cascade = CascadeType.ALL, orphanRemoval = true)
    @SQLRestriction("deleted = 'false'")
    Set<Category> children = new HashSet<>();

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Category)) {
            return false;
        }
        return id != null && id.equals(((Category) o).id);
    }

    public CategoryLookUpDTO toLookUpDTO() {
        return new CategoryLookUpDTO(getId(), getName(), getCategoryType(), getCatalogCode() != null ? this.toCatalogDTO() : null);
    }

    public CatalogDTO toCatalogDTO() {
        return new CatalogDTO(getCatalogCode(), getCatalogName(), getBarcode());
    }

    public CategoryTreeDTO toTreeDTO() {
        return new CategoryTreeDTO(getId(), getName(), getParentId(), getStatus(), getPackageCode(), getPackageName(), getVat(), getOrigin());
    }
}
