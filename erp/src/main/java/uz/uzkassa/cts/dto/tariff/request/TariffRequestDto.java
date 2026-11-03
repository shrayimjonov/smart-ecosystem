package uz.uzkassa.cts.dto.tariff.request;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import lombok.experimental.FieldDefaults;
import uz.uzkassa.cts.enums.DiscountType;
import uz.uzkassa.cts.enums.FreeTrialPeriodType;
import uz.uzkassa.cts.enums.PeriodType;
import uz.uzkassa.cts.enums.TariffSpecialType;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Getter
@Setter
@NoArgsConstructor
@ToString
@FieldDefaults(level = AccessLevel.PRIVATE)
public class TariffRequestDto implements Serializable {

    Long id;

    @NotNull
    String name;

    @NotNull
    BigDecimal price;

    @NotNull
    Double discount;

    @NotNull
    Integer period;

    @NotNull
    DiscountType discountType;

    @NotNull
    PeriodType periodType;

    @NotNull
    Integer freeTrialPeriod;

    @NotNull
    FreeTrialPeriodType freeTrialPeriodType;

    @NotNull
    TariffSpecialType tariffSpecialType;

    BigDecimal bonus = BigDecimal.ZERO;

    @NotNull
    @NotEmpty
    List<Long> serviceIds = new ArrayList<>();

    public BigDecimal getBonus() {
        return Optional.ofNullable(bonus).orElse(BigDecimal.ZERO);
    }
}
