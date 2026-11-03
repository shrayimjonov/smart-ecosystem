package uz.uzkassa.silen.dto.dashboard;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.apache.commons.lang3.StringUtils;
import uz.uzkassa.silen.enumeration.ProductType;

import java.io.Serializable;
import java.math.BigDecimal;

@Getter
@Setter
@ToString
public class StatsDTO implements Serializable {
    private ProductType type;
    private BigDecimal total;

    public StatsDTO(String type, BigDecimal total) {
        this.type = StringUtils.isNotEmpty(type) ? ProductType.valueOf(type) : null;
        this.total = total;
    }
}
