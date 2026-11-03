package uz.uzkassa.cts.dto.ctsservice.request;

import lombok.*;
import lombok.experimental.FieldDefaults;
import uz.uzkassa.cts.enums.ServiceRegistrationType;
import uz.uzkassa.cts.enums.ServiceType;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Optional;

@Getter
@Setter
@NoArgsConstructor
@ToString
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ServiceRequestDto implements Serializable {
    Long id;
    String name;
    BigDecimal price;
    ServiceRegistrationType registrationType;
    ServiceType serviceType;
    BigDecimal bonus = BigDecimal.ZERO;
    Boolean withQuantity;
    Boolean forXfile;
    String code;
    String catalogCode;

    public BigDecimal getBonus() {
        return Optional.ofNullable(bonus).orElse(BigDecimal.ZERO);
    }
}
