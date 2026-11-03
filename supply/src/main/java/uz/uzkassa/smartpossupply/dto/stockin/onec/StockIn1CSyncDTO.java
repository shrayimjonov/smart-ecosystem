package uz.uzkassa.smartpossupply.dto.stockin.onec;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

import java.io.Serializable;
import java.util.List;

@Getter
@Setter
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class StockIn1CSyncDTO implements Serializable {

    List<Long> stockInIds;

    public StockIn1CSyncDTO(List<Long> stockInIds) {
        this.stockInIds = stockInIds;
    }
}
