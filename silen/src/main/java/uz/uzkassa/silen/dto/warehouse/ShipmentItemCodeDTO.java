package uz.uzkassa.silen.dto.warehouse;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.FieldDefaults;
import uz.uzkassa.silen.enumeration.PackageType;

import java.io.Serializable;

@Getter
@Setter
@ToString
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ShipmentItemCodeDTO implements Serializable {

    @NotNull(message = "Товар не обозначен")
    Long id;

    PackageType unit;

    @NotBlank(message = "Код не может быть пустым")
    String code;

    String organizationId;

    boolean trueCheck = true;

    String barcode;

    String shipmentId;
}
