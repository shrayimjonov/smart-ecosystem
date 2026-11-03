package uz.uzkassa.silen.dto.billingpermission;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

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

    String noteFromExternalSystem;
}
