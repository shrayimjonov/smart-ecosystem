package uz.uzkassa.horeca.dto.stockout;

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
public class StockOutDetailDTO extends StockDetailBaseDTO implements Serializable {

    @Serial
    private static final long serialVersionUID = 34543543L;

    CompanyCommonDTO customer;

    List<StockOutItemDetailDTO> items = new ArrayList<>();

    @JsonIgnore
    public void addItem(StockOutItemDetailDTO item) {
        items.add(item);
    }
}
