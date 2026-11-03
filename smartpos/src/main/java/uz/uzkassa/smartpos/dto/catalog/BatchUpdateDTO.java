package uz.uzkassa.smartpos.dto.catalog;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import uz.uzkassa.smartpos.enums.ProductOrigin;

import java.io.Serializable;
import java.math.BigDecimal;

@Getter
@Setter
@ToString
public class BatchUpdateDTO implements Serializable {
    private Long categoryId;
    @NotNull
    private Long branchId;
    private BigDecimal vatRate;
    private boolean hasMark;
    private String committentTin;
    private ProductOrigin origin;
}
