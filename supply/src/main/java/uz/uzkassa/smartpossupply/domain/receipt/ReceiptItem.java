package uz.uzkassa.smartpossupply.domain.receipt;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;
import uz.uzkassa.smartpossupply.domain.product.Product;
import uz.uzkassa.smartpossupply.domain.Unit;
import uz.uzkassa.smartpossupply.domain.base.BaseEntity;

import java.io.Serial;
import java.math.BigDecimal;
import java.util.HashSet;
import java.util.Set;

/**
 * Powered by: Shuxratjon Rayimjonov
 * Date: 12.19.2022
 */
@Entity
@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
@Table(name = "receipt_item", indexes = {
        @Index(columnList = "receipt_id", name = "receipt_item_receipt_id_idx"),
        @Index(columnList = "product_id", name = "receipt_item_product_id_idx"),
})
public class ReceiptItem extends BaseEntity {

    @Serial
    private static final long serialVersionUID = 7834758734L;

    @Column(name = "receipt_id")
    Long receiptId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "receipt_id", insertable = false, updatable = false)
    Receipt receipt;

    @Column(name = "product_id")
    Long productId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", insertable = false, updatable = false)
    Product product;

    @Column(name = "unit_id")
    Long unitId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "unit_id", insertable = false, updatable = false)
    Unit unit;

    @Column(name = "comittent_tin")
    String comittentTin;

    @Column(name = "package_name", length = 1000)
    String packageName;

    @Column(name = "package_code", length = 1000)
    String packageCode;

    @Column(name = "price", precision = 25, scale = 2)
    BigDecimal price;

    @Column(name = "qty", precision = 25, scale = 2)
    BigDecimal qty;

    @Column(name = "total_price", precision = 25, scale = 2)
    BigDecimal totalPrice;

    @Column(name = "vat_rate", precision = 25, scale = 2)
    BigDecimal vatRate;

    @Column(name = "vat_price", precision = 25, scale = 2)
    BigDecimal vatPrice;

    @Column(name = "total_vat_price", precision = 25, scale = 2)
    BigDecimal totalVatPrice;

    @Column(name = "discount_percent", precision = 25, scale = 2)
    BigDecimal discountPercent;

    @Column(name = "discount_price", precision = 25, scale = 2)
    BigDecimal discountPrice;

    @Column(name = "total_discount_price", precision = 25, scale = 2)
    BigDecimal totalDiscountPrice;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "marks")
    Set<String> marks = new HashSet<>();

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "aggregation_block_codes")
    Set<String> aggregationBlockCodes = new HashSet<>();
}
