package uz.uzkassa.smartpos.domain.postgres.catalog;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.apache.commons.collections4.CollectionUtils;
import org.hibernate.annotations.DynamicUpdate;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.type.SqlTypes;
import uz.uzkassa.smartpos.domain.postgres.base.audit.AbstractAuditingEntity;
import uz.uzkassa.smartpos.domain.postgres.common.City;
import uz.uzkassa.smartpos.domain.postgres.common.Unit;
import uz.uzkassa.smartpos.domain.postgres.company.Branch;
import uz.uzkassa.smartpos.dto.CommonFileDTO;
import uz.uzkassa.smartpos.dto.SelectItem;
import uz.uzkassa.smartpos.dto.catalog.ProductDTO;
import uz.uzkassa.smartpos.dto.catalog.ProductUnitDTO;
import uz.uzkassa.smartpos.dto.product.AttributeValue;
import uz.uzkassa.smartpos.integration.tasnif.dto.MxikPackage;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.stream.Collectors;

@IdClass(ProductBranchId.class)
@Entity
@Table(name = "product_branch")
@Getter
@Setter
@DynamicUpdate
@NoArgsConstructor
@SQLDelete(sql = "UPDATE product_branch SET deleted = 'true' WHERE id=?")
public class ProductBranch extends AbstractAuditingEntity implements Serializable {
    @Id
    @NotNull
    @Column(name = "product_id", nullable = false)
    private Long productId;
    @Id
    @NotNull
    @Column(name = "branch_id", nullable = false)
    private Long branchId;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "branch_id", updatable = false, insertable = false)
    private Branch branch;
    @Id
    @Column(name = "city_id")
    private Long cityId;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "city_id", updatable = false, insertable = false, foreignKey = @ForeignKey(ConstraintMode.NO_CONSTRAINT))
    private City city;


    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "image", columnDefinition = "jsonb")
    private CommonFileDTO image;

    //    @NotNull
    @Column(name = "unit_id"/*, nullable = false*/)
    private Long unitId;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "unit_id", updatable = false, insertable = false, foreignKey = @ForeignKey(ConstraintMode.NO_CONSTRAINT))
    private Unit unit;

    //    @NotNull
    @Column(name = "category_id"/*, nullable = false*/)
    private Long categoryId;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumns(value = {
            @JoinColumn(name = "category_id", referencedColumnName = "category_id", insertable = false, updatable = false),
            @JoinColumn(name = "branch_id", referencedColumnName = "branch_id", insertable = false, updatable = false),
    })
    private CategoryBranch category;

    @Column(name = "name", columnDefinition = "TEXT")
    private String name;
    @Column(name = "barcode")
    private String barcode;
    @NotNull
    @Column(name = "vat_barcode")
    private String vatBarcode;
    @Column(name = "committent_tin", length = 14)
    private String committentTin;
    @Column(name = "description", length = 1000)
    private String description;

    @Column(name = "sales_price", precision = 25, scale = 4)
    private BigDecimal salesPrice;
    @Column(name = "vat_rate")
    private BigDecimal vatRate;
    @Column(name = "excise_amount")
    private BigDecimal exciseAmount;

    @Column(name = "favourite", columnDefinition = "boolean default false")
    private boolean favorite = false;
    @Column(name = "has_mark", columnDefinition = "boolean default false")
    private boolean hasMark = false;
    @Column(name = "has_excise", columnDefinition = "boolean default false")
    private boolean hasExcise = false;

    @Column(name = "deleted", nullable = false, columnDefinition = "boolean default false")
    private boolean deleted;

    @OneToMany(mappedBy = "productBranch", cascade = CascadeType.ALL, orphanRemoval = true)
    List<ProductUnit> units = new ArrayList<>();

    @Column(name = "credit_enabled", columnDefinition = "boolean default false")
    private boolean creditEnabled;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "credit_settings", columnDefinition = "jsonb")
    private CreditSettings creditSettings;
    @Column(name = "advance_enabled", columnDefinition = "boolean default false")
    private boolean advanceEnabled;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "advance_settings", columnDefinition = "jsonb")
    private CreditSettings advanceSettings;
    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "default_package", columnDefinition = "jsonb")
    private MxikPackage defaultPackage;
    private Short origin = 2;

    public void setOrigin(Short origin) {
        this.origin = origin != null ? origin : 2;
    }

    public void setUnits(List<ProductUnit> units) {
        if (getUnits() != null) {
            getUnits().clear();
            if (CollectionUtils.isNotEmpty(units)) {
                getUnits().addAll(units);
            }
        } else {
            this.units = units;
        }
    }

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(columnDefinition = "jsonb")
    private HashSet<AttributeValue> attributes = new HashSet<>();

    public ProductBranchId getId() {
        return new ProductBranchId(productId, branchId, cityId);
    }

    public ProductBranch(ProductBranch productBranch, Long branchId, Long cityId) {
        this.productId = productBranch.getProductId();
        this.branchId = branchId;
        this.cityId = cityId;
        this.unitId = productBranch.getUnitId();
        this.categoryId = productBranch.getCategoryId();

        this.name = productBranch.getName();
        this.barcode = productBranch.getBarcode();
        this.vatBarcode = productBranch.getVatBarcode();
        this.committentTin = productBranch.getCommittentTin();

        this.salesPrice = productBranch.getSalesPrice() != null ? productBranch.getSalesPrice() : BigDecimal.ZERO;
        this.vatRate = productBranch.getVatRate();
        this.exciseAmount = productBranch.getExciseAmount();

        this.favorite = productBranch.isFavorite();
        this.hasMark = productBranch.isHasMark();
        this.hasExcise = productBranch.isHasExcise();
        this.creditEnabled = productBranch.isCreditEnabled();
        this.creditSettings = productBranch.getCreditSettings();
        this.advanceEnabled = productBranch.isAdvanceEnabled();
        this.advanceSettings = productBranch.getAdvanceSettings();
        this.defaultPackage = productBranch.getDefaultPackage();
        this.origin = productBranch.getOrigin();
    }

    public ProductDTO getShortDTO() {
        ProductDTO productDTO = new ProductDTO();
        productDTO.setId(getProductId());
        productDTO.setName(getName());
        productDTO.setBarcode(getBarcode());
        if (getUnit() != null) {
            productDTO.setUnit(getUnit().toDTO());
        }
        return productDTO;
    }

    public ProductDTO getDTO(boolean withUnits) {
        ProductDTO productDTO = new ProductDTO();
        productDTO.setId(getProductId());
        productDTO.setBranchId(getBranchId());
        if (getUnit() != null) {
            productDTO.setUnit(getUnit().toDTO());
        }
        if (getCategory() != null) {
            productDTO.setCategoryDTO(getCategory().getDTO());
        }
        productDTO.setName(getName());
        productDTO.setBarcode(getBarcode());
        productDTO.setVatBarcode(getVatBarcode());
        productDTO.setCommittentTin(getCommittentTin());
        productDTO.setDescription(getDescription());
        productDTO.setSalesPrice(getSalesPrice());
        productDTO.setVatRate(getVatRate());
        productDTO.setExciseAmount(getExciseAmount());

        productDTO.setFavorite(isFavorite());
        productDTO.setHasMark(isHasMark());
        productDTO.setHasExcise(isHasExcise());
        productDTO.setDeleted(isDeleted());
        productDTO.setCreditEnabled(isCreditEnabled());
        productDTO.setCreditSettings(getCreditSettings());
        productDTO.setAdvanceEnabled(isAdvanceEnabled());
        productDTO.setAdvanceSettings(getAdvanceSettings());
        productDTO.setDefaultPackage(getDefaultPackage());
        productDTO.setOrigin(getOrigin());
        productDTO.setAttributes(getAttributes());
        productDTO.setImage(getImage());

        if (withUnits && CollectionUtils.isNotEmpty(getUnits())) {
            productDTO.setUnits(getUnits().stream().map(productUnit -> new ProductUnitDTO(productUnit.getProductId(), productUnit.getUnitId())).collect(Collectors.toList()));
        }
        return productDTO;
    }

    public ProductDTO getAsSelectItem() {
        ProductDTO productDTO = new ProductDTO();
        productDTO.setId(getProductId());
        productDTO.setName(getName());
        productDTO.setBarcode(getBarcode());
        productDTO.setVatBarcode(getVatBarcode());
        if (getUnit() != null) {
            productDTO.setUnit(getUnit().toDTO());
        }
        productDTO.setVatRate(getVatRate());
        productDTO.setHasMark(isHasMark());
        productDTO.setExciseAmount(getExciseAmount());
        productDTO.setDefaultPackage(getDefaultPackage());
        productDTO.setOrigin(getOrigin());
        return productDTO;
    }

    @Override
    public SelectItem toSelectItem() {
        return new SelectItem(productId, name, barcode);
    }
}
