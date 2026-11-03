package uz.uzkassa.cts.dto.stockin;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import uz.uzkassa.cts.dto.company.CompanyCommonDTO;
import uz.uzkassa.cts.dto.stock.StockDetailBaseDTO;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;


@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class StockInDetailDTO extends StockDetailBaseDTO implements Serializable {

    CompanyCommonDTO supplier;

    List<StockInItemDetailDTO> items = new ArrayList<>();

    @JsonIgnore
    public void addItem(StockInItemDetailDTO item) {
        items.add(item);
    }
}
