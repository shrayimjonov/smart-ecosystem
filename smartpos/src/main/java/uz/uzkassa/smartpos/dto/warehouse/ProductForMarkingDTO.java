package uz.uzkassa.smartpos.dto.warehouse;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import uz.uzkassa.smartpos.enums.MarkingStatus;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Objects;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ProductForMarkingDTO implements Serializable {
    private String marking;
    private Long productId;
    private Long branchId;
    private MarkingStatus status;
    private Long companyId;
    private boolean fromLocal;
    private BigDecimal costPrice;
    private String errorMessage;
    private Long incomeOfProductId;

    public BigDecimal getCostPrice() {
        return costPrice == null ? BigDecimal.ZERO : costPrice;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ProductForMarkingDTO dto = (ProductForMarkingDTO) o;
        return Objects.equals(marking, dto.marking);
    }

    @Override
    public int hashCode() {
        return Objects.hash(marking);
    }
}
