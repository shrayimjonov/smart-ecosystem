package uz.uzkassa.silen.dto.warehouse;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import uz.uzkassa.silen.domain.IncomeItem;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Set;

/**
 * A DTO for the {@link uz.uzkassa.silen.domain.IncomeItem} entity.
 */
@Getter
@Setter
@ToString
@NoArgsConstructor
public class IncomeItemDTO implements Serializable {
    private Long id;
    private Long incomeId;
    private String incomeNumber;
    private String productId;
    private String productName;
    private String packageType;
    private BigDecimal qty;
    private String serialId;
    private String serialNumber;
    private Set<String> codes;

    public IncomeItemDTO(IncomeItem item) {
        this.id = item.getId();
        this.incomeId = item.getIncomeId();
        if (item.getIncome() != null) {
            this.incomeNumber = item.getIncome().getNumber();
        }
        this.productId = item.getProductId();
        if (item.getProduct() != null) {
            this.productName = item.getProduct().getVisibleName();
            this.packageType = item.getProduct().getPackageType();
        }
        this.qty = item.getQty();
        this.serialId = item.getSerialId();
        if (item.getSerialNumber() != null) {
            this.serialNumber = item.getSerialNumber().getSerialNumber();
        }
    }
}
