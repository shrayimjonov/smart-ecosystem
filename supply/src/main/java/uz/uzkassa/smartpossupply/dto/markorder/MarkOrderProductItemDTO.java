package uz.uzkassa.smartpossupply.dto.markorder;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import uz.uzkassa.smartpossupply.enums.MarkOrderRateType;
import uz.uzkassa.smartpossupply.enums.SerialNumberType;

import java.io.Serializable;
import java.util.LinkedHashSet;
import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class MarkOrderProductItemDTO implements Serializable {

    Long productId;

    @Max(150_000)
    @Min(1)
    Integer qty;

    MarkOrderRateType rateType = MarkOrderRateType.STANDARD;

    SerialNumberType serialNumberType = SerialNumberType.OPERATOR;

    Set<String> serialNumbers = new LinkedHashSet<>();
}
