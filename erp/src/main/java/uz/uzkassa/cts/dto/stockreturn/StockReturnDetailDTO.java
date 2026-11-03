package uz.uzkassa.cts.dto.stockreturn;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import uz.uzkassa.cts.dto.stock.StockDetailBaseDTO;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;


@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class StockReturnDetailDTO extends StockDetailBaseDTO implements Serializable {

    List<StockReturnItemDetailDTO> items = new ArrayList<>();

    @JsonIgnore
    public void addItem(StockReturnItemDetailDTO item) {
        items.add(item);
    }
}
