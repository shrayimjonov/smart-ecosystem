package uz.uzkassa.cts.dto.stockreturn;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import uz.uzkassa.cts.dto.company.CompanyCommonDTO;
import uz.uzkassa.cts.dto.stock.StockListBaseDTO;

import java.io.Serializable;


@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class StockReturnListDTO extends StockListBaseDTO implements Serializable {

    CompanyCommonDTO customer;
}
