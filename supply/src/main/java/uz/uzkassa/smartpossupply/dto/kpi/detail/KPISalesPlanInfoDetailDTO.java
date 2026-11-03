package uz.uzkassa.smartpossupply.dto.kpi.detail;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import uz.uzkassa.smartpossupply.dto.base.CommonDTO;

import java.math.BigDecimal;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class KPISalesPlanInfoDetailDTO {
    CommonDTO category;
    CommonDTO product;
    BigDecimal salesPlanValue;
    BigDecimal bonusValue;
}