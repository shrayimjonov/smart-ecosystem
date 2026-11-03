package uz.uzkassa.cts.dto.modelpricehistory.request;

import jakarta.validation.constraints.NotNull;
import lombok.*;
import lombok.experimental.FieldDefaults;
import uz.uzkassa.cts.enums.CompanyType;
import uz.uzkassa.cts.enums.DeviceType;
import uz.uzkassa.cts.enums.PeriodType;

import java.math.BigDecimal;
import java.util.Optional;

@Getter
@Setter
@NoArgsConstructor
@ToString
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ModelPriceHistoryDto {

    Long id;

    @NotNull
    BigDecimal price;

    @NotNull
    Long modelId;

    @NotNull
    Integer period;

    @NotNull
    PeriodType periodType;

    @NotNull
    String name;

    @NotNull
    BigDecimal firstPaymentForLeasing;

    BigDecimal bonus = BigDecimal.ZERO;

    CompanyType companyType;

    @NotNull
    Boolean active = true;

    DeviceType deviceType;

    public BigDecimal getBonus() {
        return Optional.ofNullable(bonus).orElse(BigDecimal.ZERO);
    }
}
