package uz.uzkassa.horeca.dto.stockin;

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
public class StockInDTO extends StockBaseDTO implements Serializable {

    @Serial
    private static final long serialVersionUID = 9948938L;

    Long supplierId;

    List<StockInItemDTO> items = new ArrayList<>();
}
