package uz.uzkassa.smartpos.dto.fashion.catalog;

import lombok.Getter;
import lombok.Setter;
import uz.uzkassa.smartpos.dto.CommonFileDTO;

import java.math.BigDecimal;

@Getter
@Setter
public class CashboxProductListDto {
    private Long id;
    private String name;
    private CommonFileDTO image;
    private BigDecimal salesPrice;
}
