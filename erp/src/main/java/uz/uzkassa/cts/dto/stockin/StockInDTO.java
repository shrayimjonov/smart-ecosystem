package uz.uzkassa.cts.dto.stockin;

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
public class StockInDTO extends StockBaseDTO implements Serializable {

    Long stockTransferId;

    List<StockInItemDTO> items = new ArrayList<>();

    @JsonIgnore
    public void addItem(StockInItemDTO item) {
        items.add(item);
    }
}
