package uz.uzkassa.cts.dto.stockout;

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
public class StockOutDetailDTO extends StockDetailBaseDTO implements Serializable {

    List<StockOutItemDetailDTO> items = new ArrayList<>();

    @JsonIgnore
    public void addItem(StockOutItemDetailDTO item) {
        items.add(item);
    }
}
