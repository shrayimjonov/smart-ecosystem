package uz.uzkassa.silen.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import uz.uzkassa.silen.enumeration.DailyOperationTypes;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class DailyOperationsDTO {

    private String productId;
    private String productName;
    private String barcode;
    private BigDecimal price;

    private BigDecimal amount;
    private BigDecimal amountPrice;

    private BigDecimal startAmount;
    private BigDecimal startAmountPrice;

    private BigDecimal endAmount;
    private BigDecimal endAmountPrice;

    private DailyOperationTypes type;
    private String typeName;
    private LocalDateTime date;
}
