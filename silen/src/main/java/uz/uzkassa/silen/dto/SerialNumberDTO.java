package uz.uzkassa.silen.dto;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

import java.time.LocalDate;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class SerialNumberDTO {

    String serialNumber;

    String description;

    String productId;

    Integer startQuantity;

    LocalDate productionDate;
}
