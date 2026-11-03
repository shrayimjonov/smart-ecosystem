package uz.uzkassa.smartpos.dto.sales;

import jakarta.persistence.Transient;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import uz.uzkassa.smartpos.domain.postgres.receipt.ExtraDetail;
import uz.uzkassa.smartpos.domain.postgres.receipt.SalesDetail;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Set;

/**
 * DTO for {@link SalesDetail}
 */
@Getter
@Setter
public final class SalesDetailCreateDto implements Serializable {
    private Long productId;
    private @NotNull String productName;
    private String productBarcode;
    private @NotNull String vatBarcode;
    private String committentTin;
    private @NotNull Long unitId;
    private @NotNull String unitName;
    private @NotNull Long categoryId;
    private @NotNull String categoryName;
    private @NotNull BigDecimal price;
    private @NotNull BigDecimal qty;
    private @NotNull BigDecimal amount;
    private BigDecimal discount;
    private BigDecimal discountPercent;
    private BigDecimal vat;
    private BigDecimal vatPercent;
    private BigDecimal exciseRate;
    private BigDecimal excise;
    private Set<String> marks;
    private ExtraDetail extraDetail;
    @Transient
    private Long promotionId;
}