package uz.uzkassa.silen.dto.marking;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.FieldDefaults;
import uz.uzkassa.silen.enumeration.CisType;
import uz.uzkassa.silen.enumeration.RateType;
import uz.uzkassa.silen.enumeration.SerialNumberType;

import java.io.Serializable;
import java.util.LinkedHashSet;
import java.util.Set;

/**
 * A DTO for the {@link uz.uzkassa.silen.domain.OrderProduct} entity.
 */
@Getter
@Setter
@ToString
@FieldDefaults(level = AccessLevel.PRIVATE)
public class OrderProductItemDTO implements Serializable {
    @NotBlank
    String productId;

    RateType rateType = RateType.STANDARD;

    @Max(150_000)
    @Min(1)
    Integer quantity;

    SerialNumberType serialNumberType = SerialNumberType.OPERATOR;

    Set<String> serialNumbers = new LinkedHashSet<>();

    CisType cisType;
}
