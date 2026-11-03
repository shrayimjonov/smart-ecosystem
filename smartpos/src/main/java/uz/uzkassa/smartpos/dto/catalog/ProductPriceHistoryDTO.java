package uz.uzkassa.smartpos.dto.catalog;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import uz.uzkassa.smartpos.dto.FullNameDTO;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@ToString
public class ProductPriceHistoryDTO implements Serializable {
    private BigDecimal oldPrice;
    private BigDecimal newPrice;
    private LocalDateTime updatedDateTime;
    private FullNameDTO updatedBy;

}
