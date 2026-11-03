package uz.uzkassa.smartpossupply.domain.category;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.ColumnResult;
import jakarta.persistence.ConstructorResult;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.SqlResultSetMapping;
import jakarta.persistence.SqlResultSetMappings;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;
import uz.uzkassa.smartpossupply.domain.Branch;
import uz.uzkassa.smartpossupply.domain.base.AbstractAuditingEntity;
import uz.uzkassa.smartpossupply.domain.product.Product;
import uz.uzkassa.smartpossupply.dto.base.CommonDTO;
import uz.uzkassa.smartpossupply.dto.category.CategoryProductListDTO;
import uz.uzkassa.smartpossupply.dto.category.CategoryTreeDTO;
import uz.uzkassa.smartpossupply.dto.category.MobileCategoryListDTO;
import uz.uzkassa.smartpossupply.dto.category.MobileCategoryTreeDTO;
import uz.uzkassa.smartpossupply.dto.telegram.TelegramCategoryListMapperDTO;
import uz.uzkassa.smartpossupply.enums.CommonStatus;
import uz.uzkassa.smartpossupply.enums.EntityTypeEnum;
import uz.uzkassa.smartpossupply.enums.EnvironmentType;
import uz.uzkassa.smartpossupply.interfaces.Auditable;

import java.io.Serial;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.HashSet;
import java.util.Set;

@SqlResultSetMappings(
        {
                @SqlResultSetMapping(
                        name = "CategoryTreeDTOMapper",
                        classes = @ConstructorResult(
                                targetClass = CategoryTreeDTO.class,
                                columns = {
                                        @ColumnResult(name = "id", type = Long.class),
                                        @ColumnResult(name = "name", type = String.class),
                                        @ColumnResult(name = "parentId", type = Long.class),
                                        @ColumnResult(name = "productCount", type = BigInteger.class),
                                        @ColumnResult(name = "status", type = String.class),
                                }
                        )
                ),
                @SqlResultSetMapping(
                        name = "CategoryTreeMobileDTOMapper",
                        classes = @ConstructorResult(
                                targetClass = MobileCategoryTreeDTO.class,
                                columns = {
                                        @ColumnResult(name = "id", type = Long.class),
                                        @ColumnResult(name = "name", type = String.class),
                                        @ColumnResult(name = "productCount", type = BigInteger.class),
                                        @ColumnResult(name = "parentId", type = Long.class),
                                        @ColumnResult(name = "branchId", type = Long.class)
                                }
                        )
                ),
                @SqlResultSetMapping(
                        name = "TelegramCategoryListMapper",
                        classes = @ConstructorResult(
                                targetClass = TelegramCategoryListMapperDTO.class,
                                columns = {
                                        @ColumnResult(name = "categoryId", type = Long.class),
                                        @ColumnResult(name = "categoryName", type = String.class),
                                        @ColumnResult(name = "productId", type = Long.class),
                                        @ColumnResult(name = "productName", type = String.class),
                                        @ColumnResult(name = "vat", type = String.class),
                                        @ColumnResult(name = "price", type = BigDecimal.class),
                                        @ColumnResult(name = "foreignPrice", type = BigDecimal.class),
                                        @ColumnResult(name = "currencyType", type = String.class),
                                        @ColumnResult(name = "photoUrl", type = String.class)
                                }
                        )
                ),
                @SqlResultSetMapping(
                        name = "CategoryProductMapper",
                        classes = @ConstructorResult(
                                targetClass = CategoryProductListDTO.class,
                                columns = {
                                        @ColumnResult(name = "id", type = Long.class),
                                        @ColumnResult(name = "name", type = String.class),
                                        @ColumnResult(name = "isCategory", type = Boolean.class),
                                }
                        )
                ),
                @SqlResultSetMapping(
                        name = "MobileCategoryProductMapper",
                        classes = @ConstructorResult(
                                targetClass = MobileCategoryListDTO.class,
                                columns = {
                                        @ColumnResult(name = "id", type = Long.class),
                                        @ColumnResult(name = "name", type = String.class),
                                        @ColumnResult(name = "productCount", type = BigInteger.class),
                                }
                        )
                ),
                @SqlResultSetMapping(
                        name = "CategoryParentMapper",
                        classes = @ConstructorResult(
                                targetClass = CommonDTO.class,
                                columns = {
                                        @ColumnResult(name = "id", type = Long.class),
                                        @ColumnResult(name = "name", type = String.class)
                                }
                        )
                ),
        }
)
@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
@Entity
@Table(
        name = "category",
        indexes = {
                @Index(columnList = "name", name = "category_name_idx"),
                @Index(columnList = "catalog_code", name = "catalog_code_idx"),
                @Index(columnList = "catalog_name", name = "catalog_name_idx"),
                @Index(columnList = "branch_id", name = "category_branch_id_idx"),
                @Index(columnList = "parent_id", name = "category_parent_id_idx"),
        }
)
@SQLDelete(sql = "UPDATE category SET deleted = true WHERE id=?")
//@SQLRestriction(value = "deleted = false")
public class Category extends AbstractAuditingEntity implements Auditable {

    @Serial
    private static final long serialVersionUID = 89538L;

    @Column(name = "name", length = 2000)
    String name;

    @Enumerated(EnumType.STRING)
    @Column(name = "environment_type")
    EnvironmentType environmentType;

    @Column(name = "catalog_code")
    String catalogCode;

    @Column(name = "catalog_name", length = 2000)
    String catalogName;

    @Column(name = "branch_id")
    Long branchId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "branch_id", updatable = false, insertable = false)
    Branch branch;

    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    CommonStatus status = CommonStatus.ACTIVE;

    @Column(name = "main", columnDefinition = "boolean default false")
    boolean main = false;

    @Column(name = "parent_id")
    Long parentId;

    @ManyToOne(cascade = CascadeType.PERSIST, fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_id", updatable = false, insertable = false)
    Category parent;

    @OneToMany(mappedBy = "parent", cascade = CascadeType.ALL, orphanRemoval = true)
    @SQLRestriction("deleted = false")
    Set<Category> children = new HashSet<>();

    @ManyToMany(mappedBy = "categories")
    Set<Product> products = new HashSet<>();

    @Override
    public Long getEntityId() {
        return this.id;
    }

    @Override
    public EntityTypeEnum getEntityType() {
        return EntityTypeEnum.CATEGORY;
    }

    @Override
    public String getEntityName() {
        return getName();
    }

    public void setName(String name) {
        addLogHistory("name", this.name, name);
        this.name = name;
    }

    public void setCatalogCode(String catalogCode) {
        addLogHistory("catalogCode", this.catalogCode, catalogCode);
        this.catalogCode = catalogCode;
    }

    public void setCatalogName(String catalogName) {
        addLogHistory("catalogName", this.catalogName, catalogName);
        this.catalogName = catalogName;
    }

    public void setBranch(Branch branch) {
        addLogHistory("branch", this.branch != null ? this.branch.getName() : null, branch != null ? branch.getName() : null);
        this.branch = branch;
    }

    public void setStatus(CommonStatus status) {
        addLogHistory("status", this.status != null ? this.status.getName() : null, status != null ? status.getName() : null);
        this.status = status;
    }

    public void setMain(boolean main) {
        addLogHistory("main", this.main, main);
        this.main = main;
    }

    public void setParent(Category parent) {
        addLogHistory("parent", this.parent != null ? this.parent.getName() : null, parent != null ? parent.getName() : null);
        this.parent = parent;
    }
}
