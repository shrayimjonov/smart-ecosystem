package uz.uzkassa.cts.dto.modelpricehistory.response;

import jakarta.validation.constraints.NotNull;
import lombok.*;
import lombok.experimental.FieldDefaults;
import uz.uzkassa.cts.enums.CompanyType;
import uz.uzkassa.cts.enums.DeviceType;
import uz.uzkassa.cts.enums.OrderType;
import uz.uzkassa.cts.enums.PeriodType;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@ToString
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ModelPriceHistoryResponseDto {

    Long id;

    String name;

    @NotNull
    BigDecimal price;

    BigDecimal firstPaymentForLeasing;
    @NotNull
    Long modelId;

    @NotNull
    String modelName;

    Integer period;

    OrderType orderType;

    PeriodType periodType;

    CompanyType companyType;

    DeviceType deviceType;

    BigDecimal bonus;
    Boolean active;
}
