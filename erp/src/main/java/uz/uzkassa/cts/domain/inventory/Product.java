package uz.uzkassa.cts.domain.inventory;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.type.SqlTypes;
import uz.uzkassa.cts.domain.Reference;
import uz.uzkassa.cts.domain.Vat;
import uz.uzkassa.cts.domain.base.LocalizedEntity;
import uz.uzkassa.cts.enums.ProductType;

import java.io.Serial;
import java.math.BigDecimal;

@Entity
@Table(name = "product", indexes = {
        @Index(columnList = "category_id", name = "product_category_id_idx"),
        @Index(columnList = "brand_id", name = "product_brand_id_idx"),
        @Index(columnList = "vat_id", name = "product_vat_id_idx"),
        @Index(columnList = "unit_id", name = "product_unit_id_idx"),
        @Index(columnList = "barcode", name = "product_barcode_idx")
})
@Getter
@Setter
@SQLDelete(sql = "UPDATE product SET deleted = 'true' WHERE id=?")
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Product extends LocalizedEntity {

    @Serial
    private static final long serialVersionUID = 35125689L;

    @Column(name = "type")
    @Enumerated(EnumType.STRING)
    ProductType type;

    @Column(name = "description")
    @JdbcTypeCode(SqlTypes.LONGVARCHAR)
    String description;

    @Column(name = "barcode")
    String barcode;

    @Column(name = "sku_number")
    String skuNumber;

    @Column(name = "upc_number")
    String upcNumber;

    @Column(name = "part_number")
    String partNumber;

    @Column(name = "inventory_number")
    String inventoryNumber;

    @NotNull
    @Column(name = "price", precision = 25, scale = 2, nullable = false)
    BigDecimal price;

    @Column(name = "brand_id")
    Long brandId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "brand_id", updatable = false, insertable = false)
    Brand brand;

    @NotNull
    @Column(name = "category_id", nullable = false)
    Long categoryId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id", updatable = false, insertable = false)
    Category category;

    @Column(name = "vat_id")
    Long vatId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "vat_id", updatable = false, insertable = false)
    Vat vat;

    @Column(name = "unit_id")
    Long unitId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "unit_id", updatable = false, insertable = false)
    Reference unit;
}
