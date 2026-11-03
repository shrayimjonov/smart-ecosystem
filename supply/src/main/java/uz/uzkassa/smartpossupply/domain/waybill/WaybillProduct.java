package uz.uzkassa.smartpossupply.domain.waybill;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import org.hibernate.annotations.SQLDelete;
import uz.uzkassa.smartpossupply.domain.base.BaseEntity;
import uz.uzkassa.smartpossupply.domain.product.Product;
import uz.uzkassa.smartpossupply.dto.waybill.WaybillProductDTO;

import java.io.Serial;
import java.math.BigDecimal;

@Getter
@Setter
@Entity
@FieldDefaults(level = AccessLevel.PRIVATE)
@Table(name = "waybill_product",
        indexes = {
                @Index(columnList = "product_group_id", name = "waybill_product_product_group_id_idx"),
                @Index(columnList = "product_id", name = "waybill_product_product_id_idx"),
        }
)
@SQLDelete(sql = "UPDATE waybill_product SET deleted = true WHERE id=?")
public class WaybillProduct extends BaseEntity {

    @Serial
    private static final long serialVersionUID = 5654819831L;

    @Column(name = "product_id")
    Long productId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", updatable = false, insertable = false)
    Product product;

    @Column(name = "ord_no", length = 5)
    Integer ordNo;

    @Column(name = "committent_tin_or_pinfl")
    String committentTinOrPinfl;

    @Column(name = "committent_name")
    String committentName;

    @Column(name = "product_name", length = 2000)
    String productName;

    @Column(name = "catalog_code")
    String catalogCode;

    @Column(name = "catalog_name", length = 2000)
    String catalogName;

    @Column(name = "package_code", length = 1000)
    String packageCode;

    @Column(name = "package_name", length = 1000)
    String packageName;

    @Column(name = "amount", precision = 25, scale = 2)
    BigDecimal qty;

    @Column(name = "price", precision = 25, scale = 2)
    BigDecimal price;

    @Column(name = "delivery_sum", precision = 25, scale = 2)
    BigDecimal deliverySum;

    @Column(name = "weight_brutto", precision = 25, scale = 5)
    BigDecimal weightBrutto = BigDecimal.ZERO;

    @Column(name = "weight_netto", precision = 25, scale = 5)
    BigDecimal weightNetto = BigDecimal.ZERO;

    @Column(name = "product_group_id")
    Long productGroupId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_group_id", updatable = false, insertable = false)
    WaybillProductGroup productGroup;

    public WaybillProductDTO toDTO() {
        WaybillProductDTO dto = new WaybillProductDTO();
        dto.setId(getId());
        dto.setProductId(getProductId());
        if (getProduct() != null) {
            dto.setBranch(getProduct().getBranch().toCommonDTO());
        }
        dto.setOrdNo(getOrdNo());
        dto.setCommittentTinOrPinfl(getCommittentTinOrPinfl());
        dto.setCommittentName(getCommittentName());
        dto.setProductName(getProductName());
        dto.setCatalogCode(getCatalogCode());
        dto.setCatalogName(getCatalogName());
        dto.setPackageCode(getPackageCode());
        dto.setPackageName(getPackageName());
        dto.setAmount(getQty());
        dto.setPrice(getPrice());
        dto.setDeliverySum(getDeliverySum());
        dto.setWeightNetto(getWeightNetto());
        dto.setWeightBrutto(getWeightBrutto());
        if (getProduct() != null) {
            dto.setNetto(getProduct().getWeightNetto());
            dto.setBrutto(getProduct().getWeightBrutto());
        }
        return dto;
    }
}
