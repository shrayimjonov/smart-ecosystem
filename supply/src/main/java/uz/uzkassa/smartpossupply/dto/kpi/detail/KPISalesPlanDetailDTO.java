package uz.uzkassa.smartpossupply.dto.kpi.detail;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import uz.uzkassa.smartpossupply.dto.base.CommonDTO;
import uz.uzkassa.smartpossupply.enums.BonusType;
import uz.uzkassa.smartpossupply.enums.ProductAddType;
import uz.uzkassa.smartpossupply.enums.SalesPlanType;

import java.util.List;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class KPISalesPlanDetailDTO extends KPIDetailDTO {
    Long id;

    ProductAddType productAddType;

    SalesPlanType salesPlanType;

    BonusType bonusType;

    List<CommonDTO> employees;

    List<KPISalesPlanInfoDetailDTO> items;
}
