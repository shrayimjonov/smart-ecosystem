package uz.uzkassa.cts.dto.stock;

import lombok.Getter;
import lombok.Setter;
import uz.uzkassa.cts.dto.base.CommonDTO;

import java.io.Serializable;

@Getter
@Setter
public class StockItemProductVariationDTO extends CommonDTO implements Serializable {

    CommonDTO variationType;
}
