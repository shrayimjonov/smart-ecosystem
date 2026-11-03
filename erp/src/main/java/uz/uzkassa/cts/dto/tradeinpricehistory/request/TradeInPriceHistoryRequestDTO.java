package uz.uzkassa.cts.dto.tradeinpricehistory.request;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import uz.uzkassa.cts.enums.DeviceType;

import java.math.BigDecimal;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
@NoArgsConstructor
public class TradeInPriceHistoryRequestDTO {
    Long id;

    @NotNull
    BigDecimal price1pc;
    @NotNull
    BigDecimal price1pcWithVat;
    @NotNull
    BigDecimal vat1pc;
    @NotNull
    BigDecimal priceWithVat;
    @NotNull
    boolean active;
    @NotNull
    Long deviceModelId;
    @NotNull
    @NotEmpty
    String catalogCode;
    @NotNull
    @NotEmpty
    String catalogName;
    @NotNull
    @NotEmpty
    String packageName;
    @NotNull
    @NotEmpty
    String packageCode;
    @NotNull
    @NotEmpty
    String code;
    BigDecimal bonus;
    @NotNull
    Boolean requiresLetterOfGuarantee;
    DeviceType deviceType;
}
