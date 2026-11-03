package uz.uzkassa.cts.dto.tradeinpricehistory.response;

import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import uz.uzkassa.cts.enums.DeviceType;

import java.io.Serializable;
import java.math.BigDecimal;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
@NoArgsConstructor
public class TradeInPriceHistoryResponseDTO implements Serializable {
    Long id;

    @NotNull
    private BigDecimal price1pc;

    @NotNull
    private BigDecimal price1pcWithVat;

    @NotNull
    private BigDecimal vat1pc;

    @NotNull
    private BigDecimal priceWithVat;

    @NotNull
    private boolean active;

    @NotNull
    private Long deviceModelId;

    private String deviceModelName;

    private String catalogCode;

    private String catalogName;

    private String packageName;

    private String packageCode;

    private String code;

    private BigDecimal bonus;
    private boolean requiresLetterOfGuarantee;

    private DeviceType deviceType;
}
