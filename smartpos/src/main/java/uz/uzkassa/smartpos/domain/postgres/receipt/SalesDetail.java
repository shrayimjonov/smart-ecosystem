package uz.uzkassa.smartpos.domain.postgres.receipt;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.id.enhanced.SequenceStyleGenerator;
import org.hibernate.type.SqlTypes;
import uz.uzkassa.smartpos.domain.postgres.sales.Sales;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Set;

@Entity
@Getter
@Setter
public class SalesDetail {
    @Id
    @GenericGenerator(
            name = "sequenceGenerator",
            type = SequenceStyleGenerator.class,
            parameters = {
                    @org.hibernate.annotations.Parameter(
                            name = "optimizer",
                            value = "pooled-lo"
                    ),
                    @org.hibernate.annotations.Parameter(
                            name = "initial_value",
                            value = "1"
                    ),
                    @org.hibernate.annotations.Parameter(
                            name = "increment_size",
                            value = "1"
                    )
            }
    )
    @GeneratedValue(
            strategy = GenerationType.SEQUENCE,
            generator = "sequenceGenerator"
    )
    private Long id;

    @Version
    private Long version;

    @Column(name = "created_date_time")
    private LocalDateTime createdDateTime;

    @ManyToOne
    @JoinColumn(name = "sales_id")
    private Sales sales;

    @Column(name = "product_id")
    private Long productId;

    @Column(name = "product_name", columnDefinition = "text")
    private String productName;

    @Column(name = "product_barcode")
    private String productBarcode;

    @Column(name = "vat_barcode")
    private String vatBarcode;

    @Column(name = "committent_tin", length = 14)
    private String committentTin;

    @Column(name = "unit_id")
    private Long unitId;

    @Column(name = "unit_name")
    private String unitName;

    @Column(name = "category_id")
    private Long categoryId;

    @Column(name = "category_name", columnDefinition = "text")
    private String categoryName;

    @NotNull
    @Column(name = "price", precision = 25, scale = 2)
    private BigDecimal price;

    @NotNull
    @Column(name = "qty", scale = 3)
    private BigDecimal qty;

    @NotNull
    @Column(name = "amount", precision = 25, scale = 2)
    private BigDecimal amount;

    @Column(name = "discount")
    private BigDecimal discount;

    @Column(name = "discount_percent")
    private BigDecimal discountPercent;

    @Column(name = "vat", precision = 25, scale = 2)
    private BigDecimal vat;

    @Column(name = "vat_percent")
    private BigDecimal vatPercent;

    @Column(name = "excise_rate")
    private BigDecimal exciseRate;

    @Column(name = "excise", precision = 25, scale = 2)
    private BigDecimal excise;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(columnDefinition = "jsonb")
    private Set<String> marks;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "extra_detail", columnDefinition = "jsonb")
    private ExtraDetail extraDetail;
    @Column(name = "promotion_id")
    private Long promotionId;

    public void setSales(Sales receipt) {
        this.sales = receipt;
        this.createdDateTime = receipt.getCreatedDateTime();
    }

    public boolean sendToWarehouse() {
        return productId != null && unitId != null;
    }
}
