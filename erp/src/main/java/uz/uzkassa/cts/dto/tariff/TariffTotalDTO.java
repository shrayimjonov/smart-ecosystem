package uz.uzkassa.cts.dto.tariff;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.io.Serializable;
import java.math.BigDecimal;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
@NoArgsConstructor
@AllArgsConstructor
public class TariffTotalDTO implements Serializable {

    Long id;

    String name;

    Long total;

    BigDecimal price;

    BigDecimal bonus;
}
