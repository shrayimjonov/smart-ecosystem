package uz.uzkassa.smartpos.domain.postgres.catalog;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;
import uz.uzkassa.smartpos.dto.product.ProductColorGroupDTO;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Getter
@Setter
@Entity
@IdClass(ProductCompositeId.class)
@NoArgsConstructor
@Table(name = "product_color_group")
@SQLDelete(sql = "update product_color_group set deleted = true where branch_id = ? and generic_id = ?")
public class ProductColorGroup implements Serializable {
    @Id
    @Column(name = "generic_id")
    private Long genericId;
    @Id
    @NotNull
    @Column(name = "branch_id")
    private Long branchId;
    @NotNull
    @Column(name = "product_id")
    private Long productId;
    @Column(name = "city_id")
    private Long cityId;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumns(value = {
            @JoinColumn(
                    name = "product_id",
                    referencedColumnName = "product_id", insertable = false, updatable = false),
            @JoinColumn(
                    name = "branch_id",
                    referencedColumnName = "branch_id", insertable = false, updatable = false),
            @JoinColumn(
                    name = "city_id",
                    referencedColumnName = "city_id", insertable = false, updatable = false)
    }, foreignKey = @ForeignKey(ConstraintMode.NO_CONSTRAINT))
    private ProductBranch productBranch;
    @Column(name = "deleted", columnDefinition = "boolean default false")
    private boolean deleted = false;

    @Column(name = "color_code", length = 10, nullable = false)
    private String colorCode;

    @Column(name = "color_name")
    private String colorName;

    @Column(name = "sales_price", precision = 25, scale = 4)
    private BigDecimal salesPrice;

    @Column(name = "print_name")
    private String printName;

    @Column(name = "size_key")
    private String sizeKey;

    @OneToMany(mappedBy = "colorGroup", cascade = CascadeType.ALL, orphanRemoval = true)
    List<ColorGroupImage> images = new ArrayList<>();

    @OneToMany(mappedBy = "colorGroup", cascade = CascadeType.ALL, orphanRemoval = true)
    @SQLRestriction(value = "deleted = false")
    Set<ProductItem> productItems = new HashSet<>();

    public ProductCompositeId getId() {
        return new ProductCompositeId(getGenericId(), getBranchId());
    }

    public ProductColorGroup(Long productId, Long branchId, Long cityId, String colorCode, String colorName,
                             BigDecimal salesPrice, String printName, String sizeKey) {
        this.productId = productId;
        this.branchId = branchId;
        this.cityId = cityId;
        this.colorCode = colorCode;
        this.salesPrice = salesPrice;
        this.colorName = colorName;
        this.printName = printName;
        this.sizeKey = sizeKey;
    }

    public ProductColorGroup(ProductColorGroup colorGroup, @NotNull Long toBranchId, Long cityKeyId) {
        this.genericId = colorGroup.getGenericId();
        this.productId = colorGroup.getProductId();
        this.branchId = toBranchId;
        this.cityId = cityKeyId;
        this.colorCode = colorGroup.getColorCode();
        this.colorName = colorGroup.getColorName();
        this.salesPrice = colorGroup.getSalesPrice();
        this.printName = colorGroup.getPrintName();
        this.sizeKey = colorGroup.getSizeKey();
        this.images = colorGroup.getImages().stream().map(image -> new ColorGroupImage(image, branchId)).toList();
    }

    public List<ColorGroupImage> getImages() {
        if (images == null) {
            this.images = new ArrayList<>();
        }
        return images;
    }

    public void addImage(ColorGroupImage image) {
        image.setColorGroup(this);
        this.images.add(image);
    }

    public Set<ProductItem> getProductItems() {
        if (productItems == null) {
            this.productItems = new HashSet<>();
        }
        return productItems;
    }

    public void addProductItem(ProductItem image) {
        image.setColorGroup(this);
        if (this.productItems == null) {
            this.productItems = new HashSet<>();
        }
        this.productItems.add(image);
    }

    public ProductColorGroupDTO toDTO() {
        ProductColorGroupDTO dto = new ProductColorGroupDTO();
        dto.setId(getGenericId());
        dto.setProductId(getProductId());
        dto.setColorCode(getColorCode());
        dto.setColorName(getColorName());
        dto.setPrintName(getPrintName());
        dto.setSizeKey(getSizeKey());
        if (CollectionUtils.isNotEmpty(getImages())) {
            dto.setImages(getImages().stream().map(ColorGroupImage::toDTO).toList());
        }
        if (CollectionUtils.isNotEmpty(getProductItems())) {
            dto.setProductItems(getProductItems().stream().map(ProductItem::getDTO).toList());
        }
        return dto;
    }

    public String getName() {
        return StringUtils.isNotBlank(printName) ? colorName + " " + printName : colorName;
    }
}
