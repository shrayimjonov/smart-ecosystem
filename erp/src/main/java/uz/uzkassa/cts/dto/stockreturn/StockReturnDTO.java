package uz.uzkassa.cts.dto.stockreturn;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import uz.uzkassa.cts.dto.stock.StockBaseDTO;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class StockReturnDTO extends StockBaseDTO implements Serializable {

    Long customerId;

    List<StockReturnItemDTO> items = new ArrayList<>();
}
