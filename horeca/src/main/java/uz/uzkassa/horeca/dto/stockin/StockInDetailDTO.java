package uz.uzkassa.horeca.dto.stockin;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import uz.uzkassa.horeca.dto.company.CompanyCommonDTO;
import uz.uzkassa.horeca.dto.stock.StockDetailBaseDTO;

import java.io.Serial;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;


@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class StockInDetailDTO extends StockDetailBaseDTO implements Serializable {

    @Serial
    private static final long serialVersionUID = 4354573L;

    CompanyCommonDTO supplier;

    List<StockInItemDetailDTO> items = new ArrayList<>();

    @JsonIgnore
    public void addItem(StockInItemDetailDTO item) {
        items.add(item);
    }
}
