package uz.uzkassa.horeca.dto.stockout;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import uz.uzkassa.horeca.dto.stock.StockBaseDTO;

import java.io.Serial;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;


@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class StockOutDTO extends StockBaseDTO implements Serializable {

    @Serial
    private static final long serialVersionUID = 34543L;

    Long customerId;

    List<StockOutItemDTO> items = new ArrayList<>();
}
