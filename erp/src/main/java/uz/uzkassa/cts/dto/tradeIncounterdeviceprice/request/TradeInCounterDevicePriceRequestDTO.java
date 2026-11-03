package uz.uzkassa.cts.dto.tradeIncounterdeviceprice.request;

import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import uz.uzkassa.cts.enums.CustomerVatTypeEnum;
import uz.uzkassa.cts.enums.SmartOrOldDeviceEnum;

import java.io.Serializable;
import java.math.BigDecimal;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
@NoArgsConstructor
public class TradeInCounterDevicePriceRequestDTO  implements Serializable {
    Long id;

    @NotNull
    private BigDecimal price1pc;

    @NotNull
    private BigDecimal price1pcWithVat;

    private BigDecimal vat1pc;

    @NotNull
    private BigDecimal priceWithVat;

    @NotNull
    private boolean active;

    @NotNull
    CustomerVatTypeEnum customerVatTypeEnum;

    @NotNull
    SmartOrOldDeviceEnum smartOrOldDeviceEnum;
}
