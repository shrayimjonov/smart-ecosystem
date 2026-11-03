package uz.uzkassa.smartpossupply.dto.kpi;

import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import uz.uzkassa.smartpossupply.enums.BonusType;
import uz.uzkassa.smartpossupply.enums.ProductAddType;
import uz.uzkassa.smartpossupply.enums.SalesPlanType;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class KPISalesPlanDTO extends KPIBaseDTO {

    Long id;

    Long companyId;

    Set<Long> employeeIds = new HashSet<>();

    @NotNull(message = "{productAddType.required}")
    ProductAddType productAddType;

    @NotNull(message = "{salesPlanType.required}")
    SalesPlanType salesPlanType;

    @NotNull(message = "{bonusType.required}")
    BonusType bonusType;

    List<KPISalesPlanItemDTO> items;
}
