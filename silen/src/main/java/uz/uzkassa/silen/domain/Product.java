package uz.uzkassa.silen.domain;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Index;
import jakarta.persistence.Table;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.*;
import uz.uzkassa.silen.config.DbConstants;
import uz.uzkassa.silen.dto.ProductAttributesDTO;
import uz.uzkassa.silen.dto.ProductCommonDTO;
import uz.uzkassa.silen.dto.ProductDTO;
import uz.uzkassa.silen.dto.SelectItem;
import uz.uzkassa.silen.enumeration.ProductStatus;
import uz.uzkassa.silen.enumeration.ProductType;

import java.io.Serial;
import java.io.Serializable;
import java.math.BigDecimal;
import java.sql.Types;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

/**
 * A Product.
 */
@Getter
@Setter
@Entity
@Slf4j
@Table(schema = DbConstants.CURRENT_SCHEMA, name = "product", uniqueConstraints = {
    @UniqueConstraint(columnNames = "barcode")
},
    indexes = {
        @Index(columnList = "name", name = "product_name_idx"),
        @Index(columnList = "short_name", name = "product_name_idx"),
        @Index(columnList = "type", name = "product_type_idx"),
        @Index(columnList = "barcode", name = "product_barcode_idx")
    }
)
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
@SQLDelete(sql = "UPDATE product SET deleted = 'true' WHERE id=?")
public class Product extends BaseOrganizationEntity implements Serializable {

    @Serial
    private static final long serialVersionUID = 1983212347894L;

    @NotNull
    @Column(name = "code", nullable = false, unique = true)
    private Integer code;

    @NotNull
    @Column(name = "name", nullable = false)
    private String name;

    @NotNull
    @Column(name = "short_name", nullable = false, length = 50)
    private String shortName;

    @Column(name = "barcode")
    private String barcode;

    @Column(name = "parent_barcode")
    private String parentBarcode;

    @Column(name = "catalog_code", length = 32)
    private String catalogCode;

    @JdbcTypeCode(Types.LONGVARCHAR)
    @Column(name = "catalog_name")
    private String catalogName;

    @Column(name = "package_code")
    private String packageCode;

    @Column(name = "package_name")
    private String packageName;

    @Enumerated(EnumType.STRING)
    @Column(name = "type")
    private ProductType type;

    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    private ProductStatus status = ProductStatus.ACCEPTED;

    @ColumnDefault("0.00")
    @Column(name = "price", precision = 25, scale = 2)
    private BigDecimal price = BigDecimal.ZERO;

    @ColumnDefault("0.00")
    @Column(name = "base_price", precision = 25, scale = 2)
    private BigDecimal basePrice = BigDecimal.ZERO;

    @ColumnDefault("0.00")
    @Column(name = "profit_rate", precision = 25, scale = 2)
    private BigDecimal profitRate = BigDecimal.ZERO;

    @Column(name = "excise_rate", precision = 25, scale = 2)
    private BigDecimal exciseRate = BigDecimal.ZERO;

    @Column(name = "vat_rate", precision = 25, scale = 2)
    private BigDecimal vatRate = BigDecimal.ZERO;

    @Column(name = "catalog_price", precision = 25, scale = 2)
    private BigDecimal catalogPrice = BigDecimal.ZERO;

    @OneToMany(mappedBy = "product", fetch = FetchType.LAZY, cascade = {CascadeType.ALL}, orphanRemoval = true)
    @Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
    private Set<ProductAttributes> attributes = new HashSet<>();

    @JdbcTypeCode(Types.LONGVARCHAR)
    @Column(name = "visible_name")
    private String visibleName;

    @ColumnDefault("0")
    @Column(name = "qty_in_aggregation")
    private int qtyInAggregation;

    @ColumnDefault("0")
    @Column(name = "qty_in_block")
    private int qtyInBlock;

    @ColumnDefault("1")
    @Column(name = "origin")
    private int origin = 1;

    @ColumnDefault("0")
    @Column(name = "expired_in_month")
    private int expiredInMonth;

    @ColumnDefault("0.00")
    @Column(name = "weight_brutto", precision = 25, scale = 5)
    BigDecimal weightBrutto;
    @ColumnDefault("0.00")
    @Column(name = "weight_netto", precision = 25, scale = 5)
    BigDecimal weightNetto;

    public ProductDTO toDto() {
        ProductDTO productDTO = new ProductDTO();
        productDTO.setId(getId());
        productDTO.setName(getName());
        productDTO.setShortName(getShortName());
        productDTO.setCatalogName(getCatalogName());
        productDTO.setCatalogCode(getCatalogCode());
        productDTO.setBasePrice(getBasePrice());
        productDTO.setPrice(getPrice());
        return productDTO;
    }

    @Override
    protected SelectItem toSelectItem() {
        return new SelectItem(getId(), getName());
    }

    public ProductCommonDTO toCommonDTO() {
        return new ProductCommonDTO(getId(), getVisibleName(), getBarcode());
    }

    public void addAttribute(ProductAttributesDTO attribute, String organizationId) {
        ProductAttributes attributesEntity = new ProductAttributes();
        attributesEntity.setAttributeId(attribute.getAttributeId());
        attributesEntity.setAttributeValue(attribute.getAttributeValue());
        attributesEntity.setProductId(getId());
        attributesEntity.setProduct(this);
        attributesEntity.setOrganizationId(organizationId);
        this.attributes.add(attributesEntity);
    }

    public Double getCapacity() {
        try {
            Optional<String> attrVal = getAttributes().stream()
                .filter(attribute -> attribute.getAttribute().getSorter() == 2)
                .findFirst().map(ProductAttributes::getAttributeValue);
            log.debug("ProductId {}, attribute capacity value {}", getId(), attrVal);
            return attrVal.map(val -> val.replaceAll(",", ".")).map(Double::valueOf).orElse(0d);
        } catch (Exception e) {
            return 0d;
        }
    }

    public Double getTitleAlcohol() {
        try {
            Optional<String> attrVal = getAttributes().stream()
                .filter(attribute ->
                    attribute.getAttribute().getSorter() == 1
                ).findFirst().map(ProductAttributes::getAttributeValue);
            log.debug("ProductId {}, attribute capacity value {}", getId(), attrVal);
            return attrVal.map(Double::valueOf).orElse(0d);
        } catch (Exception e) {
            return 0d;
        }
    }

    public String getPackageType() {
        try {
            Optional<String> attrVal = getAttributes().stream()
                .filter(attribute ->
                    attribute.getAttribute().getSorter() == 3
                ).findFirst().map(ProductAttributes::getAttributeValue);
            log.debug("ProductId {}, attribute capacity value {}", getId(), attrVal);
            return attrVal.map(String::valueOf).orElse("");
        } catch (Exception e) {
            return "";
        }
    }
}
