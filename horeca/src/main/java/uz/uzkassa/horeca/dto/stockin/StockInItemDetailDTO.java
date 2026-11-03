package uz.uzkassa.horeca.dto.stockin;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import uz.uzkassa.horeca.dto.stock.StockItemDetailBaseDTO;

import java.io.Serial;
import java.io.Serializable;


@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class StockInItemDetailDTO extends StockItemDetailBaseDTO implements Serializable {

    @Serial
    private static final long serialVersionUID = 45645638L;
}
