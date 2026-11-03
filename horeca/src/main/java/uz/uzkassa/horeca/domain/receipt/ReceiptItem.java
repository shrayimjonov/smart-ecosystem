package uz.uzkassa.horeca.domain.receipt;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import org.apache.commons.lang3.StringUtils;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.type.SqlTypes;
import org.springframework.util.CollectionUtils;
import uz.uzkassa.horeca.domain.Category;
import uz.uzkassa.horeca.domain.base.BaseEntity;
import uz.uzkassa.horeca.domain.menu.MenuItem;
import uz.uzkassa.horeca.domain.product.Product;
import uz.uzkassa.horeca.domain.unit.Unit;
import uz.uzkassa.horeca.dto.terminal.TerminalReceiptDetailDTO;
import uz.uzkassa.horeca.dto.terminal.TerminalReceiptExtraDetailDTO;

import java.io.Serial;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Set;

@Entity
@Table(name = "receipt_item")
@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
@SQLDelete(sql = "UPDATE receipt_item SET deleted='true' WHERE id=?")
public class ReceiptItem extends BaseEntity implements Serializable {

    @Serial
    private static final long serialVersionUID = 34543534L;


    @Column(name = "receipt_id", nullable = false, updatable = false, insertable = false)
    Long receiptId;

    @ManyToOne(cascade = {CascadeType.PERSIST,CascadeType.MERGE}, fetch = FetchType.LAZY)
    @JoinColumn(name = "receipt_id")
    Receipt receipt;

    @NotNull
    @Column(name = "product_id", nullable = false)
    Long productId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", updatable = false, insertable = false)
    Product product;

    @Column(name = "product_name", columnDefinition = "TEXT")
    String productName;

    @Column(name = "barcode")
    String barcode;

    @Column(name = "category_id")
    Long categoryId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id", updatable = false, insertable = false)
    Category category;

    @Column(name = "catalog_code")
    String catalogCode;

    @Column(name = "catalog_name", columnDefinition = "TEXT")
    String catalogName;

    @Column(name = "menu_item_id")
    Long menuItemId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "menu_item_id", updatable = false, insertable = false)
    MenuItem menuItem;

    @Column(name = "unit_id")
    Long unitId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "unit_id", insertable = false, updatable = false)
    Unit unit;

    @Column(name = "unit_name")
    String unitName;

    @Column(name = "qty", scale = 2, precision = 25)
    BigDecimal qty;

    @Column(name = "price", scale = 4, precision = 25)
    BigDecimal price;

    @Column(name = "total_price", scale = 4, precision = 25)
    BigDecimal totalPrice;

    @Column(name = "vat_rate", scale = 2, precision = 25)
    BigDecimal vatRate;

    @Column(name = "vat_price", scale = 4, precision = 25)
    BigDecimal vatPrice;

    @Column(name = "discount", scale = 4, precision = 25)
    BigDecimal discount;

    @Column(name = "discount_percent", scale = 2, precision = 25)
    BigDecimal discountPercent;

    @Column(name = "excise", scale = 4, precision = 25)
    BigDecimal excise;

    @Column(name = "excise_rate", scale = 2, precision = 25)
    BigDecimal exciseRate;

    @Column(name = "origin")
    Integer origin = 2;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(columnDefinition = "jsonb")
    Set<String> marks;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "extra_detail", columnDefinition = "jsonb")
    TerminalReceiptExtraDetailDTO extraDetail;

}
