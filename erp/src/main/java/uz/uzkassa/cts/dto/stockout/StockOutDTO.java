package uz.uzkassa.cts.dto.stockout;

import com.fasterxml.jackson.annotation.JsonIgnore;
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
public class StockOutDTO extends StockBaseDTO implements Serializable {

    Long stockTransferId;

    List<StockOutItemDTO> items = new ArrayList<>();

    @JsonIgnore
    public void addItem(StockOutItemDTO item) {
        this.items.add(item);
    }
}
