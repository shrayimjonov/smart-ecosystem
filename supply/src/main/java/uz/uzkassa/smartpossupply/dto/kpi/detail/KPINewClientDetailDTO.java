package uz.uzkassa.smartpossupply.dto.kpi.detail;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

import java.math.BigDecimal;
import java.util.List;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class KPINewClientDetailDTO extends KPIDetailDTO {
    Long id;

    Integer clientLimit;

    BigDecimal bonusAmount;

    List<KPIPlanRouteItemDetailDTO> items;
}
