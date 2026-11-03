package uz.uzkassa.cts.dto.vehicle;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

import java.io.Serial;
import java.io.Serializable;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
@NoArgsConstructor
public class VehicleDTO implements Serializable {

    @Serial
    private static final long serialVersionUID = 7497834719345L;

    Long employeeId;

    String model;

    String color;

    String numberPlate;
}
