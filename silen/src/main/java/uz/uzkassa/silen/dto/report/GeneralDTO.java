package uz.uzkassa.silen.dto.report;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

@Getter
@Setter
@ToString
@NoArgsConstructor
public class GeneralDTO implements Serializable {
    private String organizationId;
    private String organizationName;
    private Integer deviceCount;
    private Integer lineCount;
    private String productName;
    private BigDecimal productionAmount;
    private BigDecimal realizedAmount;
    private BigDecimal balanceAmount;
    private Map<String, AmountDTO> products;

    public GeneralDTO(String organizationName) {
        this.organizationName = organizationName;
    }

    public GeneralDTO(String organizationId, String organizationName, String productName,
                      BigDecimal productionAmount, BigDecimal realizedAmount, BigDecimal balanceAmount) {
        this.organizationId = organizationId;
        this.organizationName = organizationName;
        this.productName = productName;
        this.productionAmount = productionAmount;
        this.realizedAmount = realizedAmount;
        this.balanceAmount = balanceAmount;
    }

    public GeneralDTO(String organizationName, Integer deviceCount, Integer lineCount) {
        this.organizationName = organizationName;
        this.deviceCount = deviceCount;
        this.lineCount = lineCount;
    }

    public Map<String, AmountDTO> getProducts() {
        if(products == null){
            products = new HashMap<>();
        }
        return products;
    }
}
