package uz.uzkassa.silen.dto.report;

import lombok.*;

import java.io.Serializable;
import java.math.BigDecimal;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class ProductMovementDTO implements Serializable {
    private String product;
    private BigDecimal production;
    private BigDecimal shipment;
    private BigDecimal balance;
}
