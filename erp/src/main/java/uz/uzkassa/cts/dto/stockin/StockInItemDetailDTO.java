package uz.uzkassa.cts.dto.stockin;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import uz.uzkassa.cts.dto.stock.StockItemDetailBaseDTO;

import java.io.Serializable;


@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class StockInItemDetailDTO extends StockItemDetailBaseDTO implements Serializable {

}
