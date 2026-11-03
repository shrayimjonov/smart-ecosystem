package uz.uzkassa.silen.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.GenericGenerator;
import uz.uzkassa.silen.config.DbConstants;
import uz.uzkassa.silen.dto.waybill.Products;

import java.io.Serializable;
import java.math.BigDecimal;

@Getter
@Setter
@Entity
@Table(schema = DbConstants.CURRENT_SCHEMA, name = "waybill_product")
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
//@SQLDelete(sql = "UPDATE waybill_product SET deleted = 'true' WHERE id=?")
public class WaybillProduct implements Serializable {

    @Id
    @GeneratedValue(generator = "uuid")
    @GenericGenerator(name = "uuid", strategy = "uuid2")
    protected String id;

    @Column(name = "ord_no")
    Integer ordNo;
    @Column(name = "committent_tin_or_pinfl")
    String committentTinOrPinfl;

    @Column(name = "committent_name")
    String committentName;
    @Column(name = "product_name")
    String productName;

    @Column(name = "catalog_code")
    String catalogCode;
    @Column(name = "catalog_name")
    String catalogName;
    @Column(name = "package_code")
    String packageCode;
    @Column(name = "package_name")
    String packageName;
    @Column(name = "amount")
    BigDecimal amount;

    @Column(name = "price")
    BigDecimal price;
    @Column(name = "delivery_sum")
    BigDecimal deliverySum;

    @ColumnDefault("0.00")
    @Column(name = "weight_brutto", precision = 25, scale = 5)
    BigDecimal weightBrutto;

    @ColumnDefault("0.00")
    @Column(name = "weight_netto", precision = 25, scale = 5)
    BigDecimal weightNetto;

    @Column(name = "product_group_id", insertable = false, updatable = false)
    String productGroupId;

    @Column(name = "product_id")
    String productId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", insertable = false, updatable = false)
    Product product;

    @ManyToOne/*(fetch = FetchType.LAZY)*/
    @JoinColumn(name = "product_group_id")
    WaybillProductGroups productGroup;

    public Products toDTO() {
        Products dto = new Products();
        dto.setId(getId());
        dto.setProductId(getProductId());
        dto.setOrdNo(getOrdNo());
        dto.setCommittentTinOrPinfl(getCommittentTinOrPinfl());
        dto.setCommittentName(getCommittentName());
        dto.setProductName(getProductName());
        dto.setCatalogCode(getCatalogCode());
        dto.setCatalogName(getCatalogName());
        dto.setPackageCode(getPackageCode());
        dto.setPackageName(getPackageName());
        dto.setAmount(getAmount());
        dto.setPrice(getPrice());
        dto.setDeliverySum(getDeliverySum());
        dto.setWeightNetto(getWeightNetto());
        dto.setWeightBrutto(getWeightBrutto());
        if (getProduct() != null) {
            dto.setProductWeightNetto(getProduct().getWeightNetto());
            dto.setProductWeightBrutto(getProduct().getWeightBrutto());
        }
        return dto;
    }
}
