package uz.uzkassa.smartpossupply.dto.lot;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;
import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class LotProductDTO implements Serializable {
    private Integer ordNo;
    private String productName;
    private String measureId;
    private BigDecimal count;
    private BigDecimal summa;
    private BigDecimal totalSum;
    private Long planPositionId;
    private String productCode;
    private String productProperties;
    private Integer monthId;
}
