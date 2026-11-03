package uz.uzkassa.smartpossupply.domain.product;

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
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.SqlResultSetMapping;
import jakarta.persistence.SqlResultSetMappings;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import org.apache.commons.lang3.StringUtils;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.type.SqlTypes;
import uz.uzkassa.smartpossupply.domain.Branch;
import uz.uzkassa.smartpossupply.domain.Unit;
import uz.uzkassa.smartpossupply.domain.base.AbstractAuditingEntity;
import uz.uzkassa.smartpossupply.domain.category.Category;
import uz.uzkassa.smartpossupply.domain.company.Company;
import uz.uzkassa.smartpossupply.domain.company.CompanyCommittent;
import uz.uzkassa.smartpossupply.dto.product.BlockProductDTO;
import uz.uzkassa.smartpossupply.dto.product.ProductCapacityDTO;
import uz.uzkassa.smartpossupply.dto.product.ProductCommonDTO;
import uz.uzkassa.smartpossupply.dto.product.ProductListDTO;
import uz.uzkassa.smartpossupply.dto.product.ProductListMobileDTO;
import uz.uzkassa.smartpossupply.dto.product.ProductLookUpWithWarehouseQtyDTO;
import uz.uzkassa.smartpossupply.dto.product.ProductResidueDTO;
import uz.uzkassa.smartpossupply.dto.product.TopProductDTO;
import uz.uzkassa.smartpossupply.dto.product.WebKassaProductListDTO;
import uz.uzkassa.smartpossupply.dto.stocktransfer.StockTransferProductListDTO;
import uz.uzkassa.smartpossupply.dto.telegram.TelegramTopProductListDTO;
import uz.uzkassa.smartpossupply.enums.AicAdditionalUnit;
import uz.uzkassa.smartpossupply.enums.CommonStatus;
import uz.uzkassa.smartpossupply.enums.CurrencyType;
import uz.uzkassa.smartpossupply.enums.DispenseType;
import uz.uzkassa.smartpossupply.enums.EntityTypeEnum;
import uz.uzkassa.smartpossupply.enums.ProductOrigin;
import uz.uzkassa.smartpossupply.enums.ProductType;
import uz.uzkassa.smartpossupply.enums.Vat;
import uz.uzkassa.smartpossupply.interfaces.Auditable;

import java.io.Serial;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@SqlResultSetMappings(
        {
                @SqlResultSetMapping(
                        name = "ProductResidueMapper",
                        classes = @ConstructorResult(
                                targetClass = ProductResidueDTO.class,
                                columns = {
                                        @ColumnResult(name = "id", type = Long.class),
                                        @ColumnResult(name = "name", type = String.class),
                                        @ColumnResult(name = "qty", type = BigDecimal.class),
                                        @ColumnResult(name = "percentage", type = BigDecimal.class),
                                        @ColumnResult(name = "unitName", type = String.class),
                                }
                        )
                ),
                @SqlResultSetMapping(
                        name = "TopProductMapper",
                        classes = @ConstructorResult(
                                targetClass = TopProductDTO.class,
                                columns = {
                                        @ColumnResult(name = "productId", type = Long.class),
                                        @ColumnResult(name = "productName", type = String.class),
                                        @ColumnResult(name = "percentage", type = BigDecimal.class),
                                        @ColumnResult(name = "qty", type = BigDecimal.class),
                                        @ColumnResult(name = "unitName", type = String.class)
                                }
                        )
                ),
                @SqlResultSetMapping(
                        name = "TelegramTopProductMapper",
                        classes = @ConstructorResult(
                                targetClass = TelegramTopProductListDTO.class,
                                columns = {
                                        @ColumnResult(name = "id", type = Long.class),
                                        @ColumnResult(name = "name", type = String.class),
                                        @ColumnResult(name = "vat", type = String.class),
                                        @ColumnResult(name = "price", type = BigDecimal.class),
                                        @ColumnResult(name = "foreignPrice", type = BigDecimal.class),
                                        @ColumnResult(name = "currencyType", type = String.class),
                                        @ColumnResult(name = "photoUrl", type = String.class)
                                }
                        )
                ),
                @SqlResultSetMapping(
                        name = "StockTransferProductMapper",
                        classes = @ConstructorResult(
                                targetClass = StockTransferProductListDTO.class,
                                columns = {
                                        @ColumnResult(name = "id", type = Long.class),
                                        @ColumnResult(name = "name", type = String.class),
                                        @ColumnResult(name = "capacity", type = String.class),
                                        @ColumnResult(name = "qtyInWarehouse", type = BigDecimal.class),
                                        @ColumnResult(name = "reservationQty", type = BigDecimal.class),
                                        @ColumnResult(name = "barcode", type = String.class),
                                        @ColumnResult(name = "boxBarcode", type = String.class),
                                        @ColumnResult(name = "blockBarcode", type = String.class),
                                        @ColumnResult(name = "hasMark", type = Boolean.class),
                                        @ColumnResult(name = "productType", type = String.class),
                                        @ColumnResult(name = "parentId", type = Long.class),
                                        @ColumnResult(name = "parentName", type = String.class),
                                        @ColumnResult(name = "parentBarcode", type = String.class),
                                        @ColumnResult(name = "parentVatBarcode", type = String.class),
                                        @ColumnResult(name = "brutto", type = BigDecimal.class),
                                        @ColumnResult(name = "netto", type = BigDecimal.class),
                                        @ColumnResult(name = "price", type = BigDecimal.class),
                                        @ColumnResult(name = "blockProducts", type = String.class)
                                }
                        )
                ),
                @SqlResultSetMapping(
                        name = "OrderProductLookUpMapper",
                        classes = @ConstructorResult(
                                targetClass = ProductLookUpWithWarehouseQtyDTO.class,
                                columns = {
                                        @ColumnResult(name = "id", type = Long.class),
                                        @ColumnResult(name = "name", type = String.class),
                                        @ColumnResult(name = "barcode", type = String.class),
                                        @ColumnResult(name = "vat", type = String.class),
                                        @ColumnResult(name = "favourite", type = Boolean.class),
                                        @ColumnResult(name = "unitId", type = Long.class),
                                        @ColumnResult(name = "unitCode", type = String.class),
                                        @ColumnResult(name = "unitName", type = String.class),
                                        @ColumnResult(name = "measureId", type = Long.class),
                                        @ColumnResult(name = "purchasePrice", type = BigDecimal.class),
                                        @ColumnResult(name = "price", type = BigDecimal.class),
                                        @ColumnResult(name = "foreignPrice", type = BigDecimal.class),
                                        @ColumnResult(name = "exciseRate", type = BigDecimal.class),
                                        @ColumnResult(name = "exciseSum", type = BigDecimal.class),
                                        @ColumnResult(name = "qtyInWarehouse", type = BigDecimal.class),
                                        @ColumnResult(name = "reservationQty", type = BigDecimal.class),
                                        @ColumnResult(name = "parentId", type = Long.class),
                                        @ColumnResult(name = "parentName", type = String.class),
                                        @ColumnResult(name = "parentBarcode", type = String.class),
                                        @ColumnResult(name = "parentVatBarcode", type = String.class),
                                        @ColumnResult(name = "productType", type = String.class),
                                        @ColumnResult(name = "hasMark", type = Boolean.class),
                                        @ColumnResult(name = "qtyInPackage", type = BigDecimal.class),
                                        @ColumnResult(name = "qtyInBox", type = BigDecimal.class),
                                        @ColumnResult(name = "qtyInPallet", type = BigDecimal.class),
                                        @ColumnResult(name = "brutto", type = BigDecimal.class),
                                        @ColumnResult(name = "netto", type = BigDecimal.class),
                                        @ColumnResult(name = "boxBarcode", type = String.class),
                                        @ColumnResult(name = "blockBarcode", type = String.class),
                                        @ColumnResult(name = "blockProducts", type = String.class)
                                }
                        )
                ),
                @SqlResultSetMapping(
                        name = "WebKassaProductListMapper",
                        classes = @ConstructorResult(
                                targetClass = WebKassaProductListDTO.class,
                                columns = {
                                        @ColumnResult(name = "id", type = Long.class),
                                        @ColumnResult(name = "name", type = String.class),
                                        @ColumnResult(name = "vat", type = String.class),
                                        @ColumnResult(name = "unitId", type = Long.class),
                                        @ColumnResult(name = "unitName", type = String.class),
                                        @ColumnResult(name = "unitCode", type = String.class),
                                        @ColumnResult(name = "measureId", type = Long.class),
                                        @ColumnResult(name = "price", type = BigDecimal.class),
                                        @ColumnResult(name = "qtyInWarehouse", type = BigDecimal.class),
                                        @ColumnResult(name = "barcode", type = String.class),
                                        @ColumnResult(name = "vatBarcode", type = String.class),
                                        @ColumnResult(name = "favorite", type = Boolean.class),
                                        @ColumnResult(name = "hasMark", type = Boolean.class),
                                }
                        )
                ),
                @SqlResultSetMapping(
                        name = "ProductListForOfflineMapper",
                        classes = @ConstructorResult(
                                targetClass = uz.uzkassa.smartpossupply.dto.product.ProductListForOfflineDTO.class,
                                columns = {
                                        @ColumnResult(name = "id", type = Long.class),
                                        @ColumnResult(name = "name", type = String.class),
                                        @ColumnResult(name = "barcode", type = String.class),
                                        @ColumnResult(name = "vat", type = String.class),
                                        @ColumnResult(name = "favourite", type = Boolean.class),
                                        @ColumnResult(name = "unitId", type = Long.class),
                                        @ColumnResult(name = "unitCode", type = String.class),
                                        @ColumnResult(name = "unitName", type = String.class),
                                        @ColumnResult(name = "measureId", type = Long.class),
                                        @ColumnResult(name = "purchasePrice", type = BigDecimal.class),
                                        @ColumnResult(name = "price", type = BigDecimal.class),
                                        @ColumnResult(name = "foreignPrice", type = BigDecimal.class),
                                        @ColumnResult(name = "exciseRate", type = BigDecimal.class),
                                        @ColumnResult(name = "exciseSum", type = BigDecimal.class),
                                        @ColumnResult(name = "qtyInPackage", type = BigDecimal.class),
                                        @ColumnResult(name = "qtyInBox", type = BigDecimal.class),
                                        @ColumnResult(name = "qtyInPallet", type = BigDecimal.class),
                                        @ColumnResult(name = "qtyInWarehouse", type = BigDecimal.class),
                                        @ColumnResult(name = "reservationQty", type = BigDecimal.class),
                                        @ColumnResult(name = "productType", type = String.class),
                                        @ColumnResult(name = "hasMark", type = Boolean.class),
                                        @ColumnResult(name = "brutto", type = BigDecimal.class),
                                        @ColumnResult(name = "netto", type = BigDecimal.class),
                                        @ColumnResult(name = "blockBarcode", type = String.class),
                                        @ColumnResult(name = "boxBarcode", type = String.class),
                                        @ColumnResult(name = "blockProducts", type = String.class),
                                        @ColumnResult(name = "capacity", type = String.class),
                                        @ColumnResult(name = "categoryIds", type = String.class),
                                        @ColumnResult(name = "warehouseIds", type = String.class),
                                        @ColumnResult(name = "branchIds", type = String.class)
                                }
                        )
                ),
                @SqlResultSetMapping(
                        name = "MobileProductListMapper",
                        classes = @ConstructorResult(
                                targetClass = ProductListMobileDTO.class,
                                columns = {
                                        @ColumnResult(name = "id", type = Long.class),
                                        @ColumnResult(name = "name", type = String.class),
                                        @ColumnResult(name = "unitId", type = Long.class),
                                        @ColumnResult(name = "unitName", type = String.class),
                                        @ColumnResult(name = "price", type = BigDecimal.class),
                                        @ColumnResult(name = "qtyInPackage", type = BigDecimal.class),
                                        @ColumnResult(name = "qtyInBox", type = BigDecimal.class),
                                        @ColumnResult(name = "qtyInPallet", type = BigDecimal.class),
                                        @ColumnResult(name = "qty", type = BigDecimal.class)
                                }
                        )
                ),
                @SqlResultSetMapping(
                        name = "ProductCapacityDTOMapper",
                        classes = @ConstructorResult(
                                targetClass = ProductCapacityDTO.class,
                                columns = {
                                        @ColumnResult(name = "id", type = Long.class),
                                        @ColumnResult(name = "name", type = String.class),
                                        @ColumnResult(name = "capacity", type = String.class)
                                }
                        )
                )
        }
)
@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
@Entity
@Table(
        name = "product",
        indexes = {
                @Index(columnList = "name", name = "product_name_idx"),
                @Index(columnList = "barcode", name = "product_barcode_idx"),
                @Index(columnList = "vat_barcode", name = "product_vat_barcode_idx"),
                @Index(columnList = "category_id", name = "product_category_id_idx"),
                @Index(columnList = "branch_id", name = "product_branch_id_idx"),
                @Index(columnList = "company_id", name = "product_company_id_idx"),
                @Index(columnList = "product_type", name = "product_type_idx"),
                @Index(columnList = "deleted", name = "product_deleted_idx"),
                @Index(columnList = "status", name = "product_status_idx")
        }
)
@SQLDelete(sql = "UPDATE product SET deleted = true WHERE id=?")
//@SQLRestriction(value = "deleted = false")
public class Product extends AbstractAuditingEntity implements Auditable {

    @Serial
    private static final long serialVersionUID = 13L;

    @Column(name = "tasnif_id")
    String tasnifId;

    @Column(name = "name", length = 2000)
    String name;

    @Column(name = "short_name", length = 2000)
    String shortName;

    @Column(name = "brand")
    String brand;

    @Column(name = "barcode")
    String barcode; // Shtrix code

    @Column(name = "vat_barcode")
    String vatBarcode; // = ИКПУ = mxik = catalog code

    @Column(name = "catalog_name", length = 2000)
    String catalogName;

    @Column(name = "package_name", length = 1000)
    String packageName;

    @Column(name = "package_code", length = 1000)
    String packageCode;

    @Column(name = "qty_in_package", precision = 25, scale = 2)
    BigDecimal qtyInPackage;// this field should be considered as qty in block.

    @Column(name = "qty_in_box", precision = 25, scale = 2)
    BigDecimal qtyInBox;// qty in box

    @Column(name = "qty_in_pallet", precision = 25, scale = 2)
    BigDecimal qtyInPallet;// qty in pallet

    @Column(name = "capacity")
    String capacity;

    @Column(name = "product_type")
    @Enumerated(value = EnumType.STRING)
    ProductType productType;

    @Column(name = "unit_id")
    Long unitId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "unit_id", updatable = false, insertable = false)
    Unit unit;

    @Column(name = "vat")
    @Enumerated(value = EnumType.STRING)
    Vat vat = Vat.WITHOUT_VAT;

    @Column(name = "purchase_price", precision = 25, scale = 2)
    BigDecimal purchasePrice;

    @Column(name = "price", precision = 25, scale = 2)
    BigDecimal price;

    @Column(name = "foreign_price", precision = 25, scale = 2)
    BigDecimal foreignPrice;

    @Column(name = "currency_type")
    @Enumerated(value = EnumType.STRING)
    CurrencyType currencyType;

    @Column(name = "exchange_rate", precision = 25, scale = 2)
    BigDecimal exchangeRate;

    @Column(name = "category_id")
    Long categoryId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id", updatable = false, insertable = false)
    Category category;

    @ManyToMany
    @JoinTable(
            name = "product_category",
            joinColumns = {@JoinColumn(name = "product_id", referencedColumnName = "id")},
            inverseJoinColumns = {@JoinColumn(name = "category_id", referencedColumnName = "id")})
    Set<Category> categories = new HashSet<>();

    @NotNull
    @Column(name = "branch_id", nullable = false)
    Long branchId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "branch_id", updatable = false, insertable = false)
    Branch branch;

    @Column(name = "company_id")
    Long companyId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "company_id", updatable = false, insertable = false)
    Company company;

    @Column(name = "company_committent_id")
    Long companyCommittentId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "company_committent_id", updatable = false, insertable = false)
    CompanyCommittent companyCommittent;

    @Column(name = "favourite", columnDefinition = "boolean default false")
    boolean favourite = false;

    @Column(name = "has_mark", columnDefinition = "boolean default false")
    boolean hasMark = false;

    @Column(name = "discount", columnDefinition = "boolean default false")
    boolean discount = false;

    @Column(name = "tnved")
    String tnved;

    @Column(name = "box_barcode")
    String boxBarcode;

    @Column(name = "block_barcode")
    String blockBarcode;

    @Column(name = "origin")
    @Enumerated(EnumType.STRING)
    ProductOrigin origin;

    @Column(name = "parent_id")
    Long parentId;

    @ManyToOne(cascade = CascadeType.PERSIST, fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_id", updatable = false, insertable = false)
    Product parent;

    @OneToMany(mappedBy = "product", cascade = CascadeType.ALL, orphanRemoval = true)
    Set<ProductPhoto> photos = new HashSet<>();

    @Column(name = "status")
    @Enumerated(EnumType.STRING)
    CommonStatus status = CommonStatus.ACTIVE;

    @Column(name = "weight_brutto", precision = 25, scale = 6)
    BigDecimal weightBrutto;

    @Column(name = "weight_netto", precision = 25, scale = 6)
    BigDecimal weightNetto;

    @Column(name = "excise_rate", precision = 25, scale = 2)
    BigDecimal exciseRate = BigDecimal.ZERO;

    @Column(name = "excise_sum", precision = 25, scale = 2)
    BigDecimal exciseSum = BigDecimal.ZERO;

    @Enumerated(EnumType.STRING)
    @Column(name = "additional_unit")
    AicAdditionalUnit additionalUnit;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "block_products")
    List<BlockProductDTO> blockProducts = new ArrayList<>();

    @Column(name = "expired_in_month")
    Integer expiredInMonth;

    @Enumerated(EnumType.STRING)
    @Column(name = "dispense_type")
    DispenseType dispenseType;

    @OneToMany(mappedBy = "product", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    Set<ProductDetail> details = new HashSet<>();

    public ProductDetail getDetail() {
        return details.stream().findFirst().orElse(null);
    }

    public void setBarcode(String barcode) {
        addLogHistory("barcode", this.barcode, barcode);
        this.barcode = barcode != null ? barcode.trim() : null;
    }

    public void setVatBarcode(String vatBarcode) {
        addLogHistory("vatBarcode", this.vatBarcode, vatBarcode);
        this.vatBarcode = vatBarcode != null ? vatBarcode.trim() : null;
    }

    public void setName(String name) {
        addLogHistory("name", this.name, name);
        this.name = name;
    }

    public void setBrand(String brand) {
        addLogHistory("brand", this.brand, brand);
        this.brand = brand;
    }

    public void setCatalogName(String catalogName) {
        addLogHistory("catalogName", this.catalogName, catalogName);
        this.catalogName = catalogName;
    }

    public void setPackageName(String packageName) {
        addLogHistory("packageName", this.packageName, packageName);
        this.packageName = packageName;
    }

    public void setPackageCode(String packageCode) {
        addLogHistory("packageCode", this.packageCode, packageCode);
        this.packageCode = packageCode;
    }

    public void setQtyInPackage(BigDecimal qtyInPackage) {
        addLogHistory("qtyInPackage", this.qtyInPackage, qtyInPackage);
        this.qtyInPackage = qtyInPackage;
    }

    public void setCapacity(String capacity) {
        addLogHistory("capacity", this.capacity, capacity);
        this.capacity = capacity;
    }

    public void setProductType(ProductType productType) {
        addLogHistory("productType", this.productType, productType);
        this.productType = productType;
    }

    public void setOrigin(ProductOrigin origin) {
        addLogHistory("origin", this.origin, origin);
        this.origin = origin;
    }

    public void setPrice(BigDecimal price) {
        addLogHistory("price", this.price, price);
        this.price = price;
    }

    public ProductCommonDTO toProductCommonDTO() {
        return new ProductCommonDTO(getId(), getName(), getShortName(), getBarcode(), getVatBarcode());
    }

    public ProductListDTO toListDTO() {
        ProductListDTO productListDTO = new ProductListDTO();
        productListDTO.setId(getId());
        productListDTO.setName(getName());
        productListDTO.setShortName(getShortName());
        productListDTO.setBarcode(getBarcode());
        productListDTO.setVatBarcode(getVatBarcode());
        productListDTO.setCategories(getCategories().stream().map(Category::toCommonDTO).toList());
        productListDTO.setBranch(getBranch().toCommonDTO());
        productListDTO.setVat(getVat());
        productListDTO.setUnit(getUnit().toDTO());
        productListDTO.setPrice(getPrice());
        productListDTO.setCapacity(getCapacity());
        productListDTO.setHasMark(isHasMark());
        productListDTO.setStatus(getStatus());
        productListDTO.setBrutto(getWeightBrutto());
        productListDTO.setNetto(getWeightNetto());
        productListDTO.setQtyInPackage(getQtyInPackage());
        productListDTO.setBlockProducts(getBlockProducts());
        return productListDTO;
    }

    public String getCatalogCode() {
        return this.getVatBarcode();
    }

    @Override
    public Long getEntityId() {
        return this.id;
    }

    @Override
    public EntityTypeEnum getEntityType() {
        return EntityTypeEnum.PRODUCT;
    }

    @Override
    public String getEntityName() {
        return getName();
    }

    public AicAdditionalUnit getAdditionalUnit() {
        if (additionalUnit == null) {
            if (StringUtils.isNotEmpty(this.getVatBarcode()) && (ProductType.Cigarette.equals(this.getProductType()) || ProductType.CigaretteBlock.equals(this.getProductType()))) {
                if (this.getVatBarcode().startsWith("02402")) {
                    additionalUnit = AicAdditionalUnit.UNIT_1000;
                } else if (this.getVatBarcode().startsWith("08543")) {
                    additionalUnit = AicAdditionalUnit.UNIT;
                }
            } else if (StringUtils.isNotEmpty(this.getVatBarcode()) && (ProductType.Alcohol.equals(this.getProductType()) || ProductType.AlcoholBlock.equals(this.getProductType()))) {
                if (!(this.getVatBarcode().startsWith("02204") || this.getVatBarcode().startsWith("02205"))) {
                    additionalUnit = AicAdditionalUnit.LITRE_100;
                } else {
                    additionalUnit = AicAdditionalUnit.LITRE;
                }
            } else if (ProductType.Pharma.equals(getProductType())) {
                additionalUnit = AicAdditionalUnit.NONE;
            } else if (ProductType.Beer.equals(productType) || ProductType.BeerBlock.equals(productType) || ProductType.Water.equals(productType)) {
                additionalUnit = AicAdditionalUnit.LITRE;
            } else {
                additionalUnit = AicAdditionalUnit.UNIT;
            }
        }
        return additionalUnit;
    }

    public void setAdditionalUnit() {
        AicAdditionalUnit additionalUnit = null;
        if (StringUtils.isNotEmpty(this.getVatBarcode()) && (ProductType.Cigarette.equals(this.getProductType()) || ProductType.CigaretteBlock.equals(this.getProductType()))) {
            if (this.getVatBarcode().startsWith("02402")) {
                additionalUnit = AicAdditionalUnit.UNIT_1000;
            } else if (this.getVatBarcode().startsWith("08543")) {
                additionalUnit = AicAdditionalUnit.UNIT;
            }
        } else if (StringUtils.isNotEmpty(this.getVatBarcode()) && (ProductType.Alcohol.equals(this.getProductType()) || ProductType.AlcoholBlock.equals(this.getProductType()))) {
            if (!(this.getVatBarcode().startsWith("02204") || this.getVatBarcode().startsWith("02205"))) {
                additionalUnit = AicAdditionalUnit.LITRE_100;
            } else {
                additionalUnit = AicAdditionalUnit.LITRE;
            }
        } else if (ProductType.Pharma.equals(getProductType())) {
            additionalUnit = AicAdditionalUnit.NONE;
        } else if (ProductType.Beer.equals(productType) || ProductType.BeerBlock.equals(productType) || ProductType.Water.equals(productType)) {
            additionalUnit = AicAdditionalUnit.LITRE;
        } else {
            additionalUnit = AicAdditionalUnit.UNIT;
        }
        this.additionalUnit = additionalUnit;
    }
}
