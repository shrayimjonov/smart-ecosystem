package uz.uzkassa.silen.dto.marking;

import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.FieldDefaults;
import uz.uzkassa.silen.enumeration.CisType;
import uz.uzkassa.silen.enumeration.SerialNumberType;

import java.io.Serializable;
import java.util.Set;

/**
 * A DTO for the {@link uz.uzkassa.silen.domain.OrderProduct} entity.
 */
@Getter
@Setter
@ToString
@FieldDefaults(level = AccessLevel.PRIVATE)
public class TuronOrderProductDTO implements Serializable {

    @NotNull
    String gtin;

    Integer rateType;

    @NotNull
    Integer quantity;

    SerialNumberType serialNumberType;

    String stickerId;

    @NotNull
    Integer templateId;

    CisType cisType;

    Set<String> serialNumbers;

}
