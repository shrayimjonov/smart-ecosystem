package uz.uzkassa.smartpossupply.dto.trip;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

import java.util.Set;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class OrderTripDTO {

    @NotNull
    Long id;

    @NotEmpty
    Set<Long> orderIds;
}
