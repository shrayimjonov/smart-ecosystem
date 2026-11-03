package uz.uzkassa.cts.dto.tariff.request;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.io.Serializable;

@Getter
@Setter
@NoArgsConstructor
@ToString
public class TariffServiceDto implements Serializable {


    @NotNull
    private Long tariffId;
    @NotNull
    private Long serviceId;

}
