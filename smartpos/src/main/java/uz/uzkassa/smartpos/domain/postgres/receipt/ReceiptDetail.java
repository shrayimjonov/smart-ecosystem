package uz.uzkassa.smartpos.domain.postgres.receipt;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.id.enhanced.SequenceStyleGenerator;
import org.hibernate.type.SqlTypes;
import uz.uzkassa.smartpos.dto.receipt.ReceiptDetailDTO;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Set;

@Entity
@Getter
@Setter
@Table(name = "receipt_detail")
public class ReceiptDetail implements Cloneable {

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

    @ManyToOne
    @JoinColumn(name = "receipt_id")
    private Receipt receipt;

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
    @Column(name = "price", precision = 25, scale = 4)
    private BigDecimal price;

    @NotNull
    @Column(name = "qty", scale = 3)
    private BigDecimal qty;

    @NotNull
    @Column(name = "amount", precision = 25, scale = 4)
    private BigDecimal amount;

    @Column(name = "discount")
    private BigDecimal discount;

    @Column(name = "discount_percent")
    private BigDecimal discountPercent;

    @Column(name = "nds", precision = 25, scale = 4)
    private BigDecimal nds;

    @Column(name = "nds_percent")
    private BigDecimal ndsPercent;

    @Column(name = "excise_rate")
    private BigDecimal exciseRate;

    @Column(name = "excise", precision = 25, scale = 4)
    private BigDecimal excise;

    @ElementCollection(fetch = FetchType.LAZY)
    @CollectionTable(name = "detail_marks")
    private Set<String> marks;

    @Column(name = "receipt_date_time")
    private LocalDateTime receiptDateTime;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "extra_detail", columnDefinition = "jsonb")
    private ExtraDetail extraDetail;

    public Long getReceiptId() {
        return getReceipt() != null ? getReceipt().getId() : null;
    }

    public ReceiptDetailDTO toDTO(boolean withMarks) {
        ReceiptDetailDTO detail = new ReceiptDetailDTO();
        detail.setProductId(getProductId());
        detail.setReceiptId(getReceipt().getId());
        detail.setProductName(getProductName());
        if (StringUtils.isNotEmpty(getProductBarcode())) {
            if (getProductBarcode().contains("$")) {
                detail.setProductBarcode(StringUtils.split(getProductBarcode(), "$")[0]);
            } else {
                detail.setProductBarcode(getProductBarcode());
            }
        }
        if (StringUtils.isNotEmpty(getVatBarcode())) {
            detail.setVatBarcode(getVatBarcode());
        } else if (StringUtils.isNotEmpty(getProductBarcode()) && getProductBarcode().contains("$")) {
            detail.setVatBarcode(StringUtils.split(getProductBarcode(), "$")[1]);
        }
        detail.setCommittentTin(getCommittentTin());
        detail.setUnitId(getUnitId());
        detail.setUnitName(getUnitName());
        detail.setCategoryId(getCategoryId());
        detail.setCategoryName(getCategoryName());
        detail.setExcise(getExcise());
        detail.setExciseRate(getExciseRate());
        detail.setPrice(getPrice());
        detail.setQty(getQty());
        detail.setAmount(getAmount());
        detail.setDiscount(getDiscount());
        detail.setDiscountPercent(getDiscountPercent());
        detail.setNds(getNds());
        detail.setNdsPercent(getNdsPercent());
        if (withMarks && CollectionUtils.isNotEmpty(getMarks())) {
            detail.setMarks(getMarks());
        }
        detail.setExtraDetail(getExtraDetail());
        return detail;
    }

    public Set<String> getMarks() {
        if (CollectionUtils.isNotEmpty(this.marks)) {
            return this.marks;
        }
        return null;
    }

    public boolean sendRegister() {
        return productId != null && unitId != null && qty != null;
    }

    @Override
    public ReceiptDetail clone() {
        ReceiptDetail clone = new ReceiptDetail();
        clone.setProductId(getProductId());
        clone.setProductName(getProductName());
        clone.setProductBarcode(getProductBarcode());
        clone.setVatBarcode(getVatBarcode());
        clone.setCommittentTin(getCommittentTin());
        clone.setUnitId(getUnitId());
        clone.setUnitName(getUnitName());
        clone.setCategoryId(getCategoryId());
        clone.setCategoryName(getCategoryName());
        clone.setPrice(getPrice());
        clone.setQty(getQty());
        clone.setAmount(getAmount());
        clone.setDiscount(getDiscount());
        clone.setDiscountPercent(getDiscountPercent());
        clone.setNds(getNds());
        clone.setNdsPercent(getNdsPercent());
        clone.setExciseRate(getExciseRate());
        clone.setExcise(getExcise());
        clone.setMarks(getMarks());
        clone.setReceiptDateTime(getReceiptDateTime());
        clone.setExtraDetail(getExtraDetail());
        return clone;
    }
}
