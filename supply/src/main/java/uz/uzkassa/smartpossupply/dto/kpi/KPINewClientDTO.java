package uz.uzkassa.smartpossupply.dto.kpi;

import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

import java.math.BigDecimal;
import java.util.Set;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class KPINewClientDTO extends KPIBaseDTO {

    Long id;

    Long companyId;

    @NotNull(message = "client limit can not be empty")
    Integer clientLimit;

    @NotNull(message = "bonus amount doesn't show")
    BigDecimal bonusAmount;

    Set<Long> planRouteIds;
}
