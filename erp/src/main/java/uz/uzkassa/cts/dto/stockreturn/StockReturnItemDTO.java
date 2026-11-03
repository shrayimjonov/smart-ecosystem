package uz.uzkassa.cts.dto.stockreturn;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import uz.uzkassa.cts.dto.stock.StockItemBaseDTO;

import java.io.Serializable;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class StockReturnItemDTO extends StockItemBaseDTO implements Serializable {


}
