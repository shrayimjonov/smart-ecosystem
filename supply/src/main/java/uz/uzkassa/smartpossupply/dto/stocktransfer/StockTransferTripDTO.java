package uz.uzkassa.smartpossupply.dto.stocktransfer;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

import java.util.Set;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
@AllArgsConstructor
public class StockTransferTripDTO {

    @NotNull
    Long id;

    @NotEmpty
    Set<Long> orderIds;

    Long tripId;
}
