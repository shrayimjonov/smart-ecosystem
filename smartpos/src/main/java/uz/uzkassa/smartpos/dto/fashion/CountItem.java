package uz.uzkassa.smartpos.dto.fashion;

import lombok.*;

import java.io.Serializable;
import java.math.BigDecimal;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CountItem implements Serializable {
    private Long id;
    private String sizeCode;
    private String sizeValue;
    private Integer sorder;
    private BigDecimal count;
}
