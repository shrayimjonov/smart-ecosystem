package uz.uzkassa.cts.dto.vehicle;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import uz.uzkassa.cts.dto.base.CommonDTO;
import uz.uzkassa.cts.enums.CommonStatus;

import java.io.Serial;
import java.io.Serializable;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
@NoArgsConstructor
public class VehicleListDTO implements Serializable {

    @Serial
    private static final long serialVersionUID = 2846328321345L;

    Long id;

    CommonDTO employee;

    String model;

    String color;

    String numberPlate;

    CommonStatus status;
}
