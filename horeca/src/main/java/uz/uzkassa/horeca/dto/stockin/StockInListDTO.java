package uz.uzkassa.horeca.dto.stockin;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import uz.uzkassa.horeca.dto.base.CommonDTO;
import uz.uzkassa.horeca.dto.stock.StockListBaseDTO;

import java.io.Serial;
import java.io.Serializable;


@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class StockInListDTO extends StockListBaseDTO implements Serializable {

    @Serial
    private static final long serialVersionUID = 98893485L;

    CommonDTO supplier;
}
