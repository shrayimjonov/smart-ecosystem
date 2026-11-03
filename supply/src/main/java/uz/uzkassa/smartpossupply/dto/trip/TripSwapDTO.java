package uz.uzkassa.smartpossupply.dto.trip;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class TripSwapDTO {
    @NonNull
    Long fromTripId;

    @NonNull
    Long toTripId;

    @NonNull
    Long fromStockTransferId;

    @NonNull
    Long toStockTransferId;
}
