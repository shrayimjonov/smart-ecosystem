package uz.uzkassa.smartpos.domain.postgres.catalog;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.apache.commons.collections4.CollectionUtils;
import org.hibernate.annotations.DynamicUpdate;
import org.hibernate.annotations.SQLDelete;
import uz.uzkassa.smartpos.domain.postgres.FileUpload;
import uz.uzkassa.smartpos.domain.postgres.base.audit.AbstractAuditingEntity;
import uz.uzkassa.smartpos.dto.SelectItem;
import uz.uzkassa.smartpos.dto.product.ProductItemDTO;
import uz.uzkassa.smartpos.dto.product.ProductItemSearchDTO;
import uz.uzkassa.smartpos.utils.ServerUtils;

import java.io.Serializable;
import java.math.BigDecimal;

@Entity
@Table(name = "product_item")
@Getter
@Setter
@IdClass(ProductCompositeId.class)
@DynamicUpdate
@NoArgsConstructor
@SQLDelete(sql = "UPDATE product_item SET deleted = 'true' WHERE branch_id = ? and generic_id = ?")
public class ProductItem extends AbstractAuditingEntity implements Serializable {
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

    @Column(name = "color_group_id")
    private Long colorGroupId;
    @ManyToOne(cascade = CascadeType.PERSIST, fetch = FetchType.LAZY)
    @JoinColumns(value = {
            @JoinColumn(name = "color_group_id", referencedColumnName = "generic_id", insertable = false, updatable = false),
            @JoinColumn(name = "branch_id", referencedColumnName = "branch_id", insertable = false, updatable = false),
    }, foreignKey = @ForeignKey(ConstraintMode.NO_CONSTRAINT))
    private ProductColorGroup colorGroup;

    @Column(name = "name", columnDefinition = "TEXT")
    private String name;

    @NotNull
    @Column(name = "barcode", nullable = false, unique = true)
    private String barcode;

    @Column(name = "sales_price", precision = 25, scale = 4)
    private BigDecimal salesPrice;

    private Boolean deleted = false;

    private String sizeKey;
    private String sizeCode;
    private String sizeValue;
    private Integer sorder;

    @Override
    public ProductCompositeId getId() {
        return new ProductCompositeId(genericId, branchId);
    }

    public ProductItem(ProductItem productItem, Long toBranchId, Long cityKeyId) {
        this.genericId = productItem.getGenericId();
        this.productId = productItem.getProductId();
        this.branchId = toBranchId;
        this.cityId = cityKeyId;
        this.name = productItem.getName();
        this.barcode = ServerUtils.generateUniqueBarcode();
        this.salesPrice = productItem.getSalesPrice();
        this.sizeKey = productItem.getSizeKey();
        this.sizeCode = productItem.getSizeCode();
        this.sizeValue = productItem.getSizeValue();
        this.sorder = productItem.getSorder();
        this.productBranch = productItem.getProductBranch();
        this.colorGroupId = productItem.getColorGroupId();
    }

    public ProductItemDTO getDTO() {
        final ProductItemDTO dto = new ProductItemDTO();
        dto.setId(getGenericId());
        dto.setDeleted(getDeleted());
        if (getColorGroup() != null) {
            dto.setColorGroupId(getColorGroup().getGenericId());
            dto.setColorGroupName(getColorGroup().getColorName());
            dto.setColorGroupCode(getColorGroup().getColorCode());
            dto.setColorGroupPrintName(getColorGroup().getPrintName());
        }
        dto.setColorGroupId(getColorGroupId());
        dto.setProductId(getProductId());
        dto.setName(getName());
        dto.setBarcode(getBarcode());
        dto.setSalesPrice(getSalesPrice());
        dto.setSizeKey(getSizeKey());
        dto.setSizeCode(getSizeCode());
        dto.setSizeValue(getSizeValue());
        dto.setSorder(getSorder());
        return dto;
    }

    public ProductItemSearchDTO getSearchDto() {
        final ProductItemSearchDTO dto = new ProductItemSearchDTO();
        dto.setId(getGenericId());
        dto.setBranchId(getBranchId());
        dto.setName(getName());
        if (getProductBranch() != null) {
            dto.setProduct(getProductBranch().getDTO(false));
        }
        dto.setBarcode(getBarcode());
        dto.setSizeName(getSizeValue());
        if (getColorGroup() != null) {
            final ProductColorGroup colorGroup = getColorGroup();
            dto.setColorCode(colorGroup.getColorCode());
            dto.setColorName(colorGroup.getColorName());
            dto.setPrintName(colorGroup.getPrintName());
            dto.setSalesPrice(colorGroup.getSalesPrice());
            if (CollectionUtils.isNotEmpty(colorGroup.getImages())) {
                dto.setImageUrl(colorGroup.getImages()
                        .stream()
                        .filter(ColorGroupImage::isMain)
                        .findAny()
                        .map(ColorGroupImage::getFile)
                        .map(FileUpload::getUrl)
                        .orElse(null));
            }
        }
        return dto;
    }

    @Override
    public SelectItem toSelectItem() {
        return new SelectItem(genericId, name, barcode);
    }
}
