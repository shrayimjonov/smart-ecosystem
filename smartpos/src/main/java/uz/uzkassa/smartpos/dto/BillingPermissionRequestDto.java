package uz.uzkassa.smartpos.dto;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import uz.uzkassa.smartpos.dto.integration.BillingCategoryDTO;
import uz.uzkassa.smartpos.integration.billing.enums.XizmatType;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class BillingPermissionRequestDto {

    Integer unitCount;

    BigDecimal amount;

    LocalDateTime startDate;

    LocalDateTime endDate;

    @NotEmpty
    @NotNull
    String tin;

    @NotNull
    String serviceType;

    String pinfl;
    BillingCategoryDTO categoryDTO;
    BigDecimal bonusAmount;
    XizmatType xizmatType = XizmatType.GROUPED;
}
