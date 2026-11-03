package uz.uzkassa.silen.dto.mongo;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.FieldDefaults;
import uz.uzkassa.silen.enumeration.PackageType;

import java.io.Serializable;

/**
 * A DTO for the {@link uz.uzkassa.silen.domain.mongo.ShipmentMark} entity.
 */
@Getter
@Setter
@ToString
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ShipmentMarkDTO implements Serializable {

    String id;

    String code;

    String printCode;

    Long shipmentItemId;

    PackageType unit;

    Integer quantity;

}
