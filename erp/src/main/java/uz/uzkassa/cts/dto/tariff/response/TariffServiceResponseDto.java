package uz.uzkassa.cts.dto.tariff.response;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import uz.uzkassa.cts.dto.ctsservice.response.ServiceResponseDto;
import uz.uzkassa.cts.enums.FreeTrialPeriodType;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@ToString
public class TariffServiceResponseDto implements Serializable {


    private Long id;
    private String name;
    private BigDecimal price;
    private Double discount;
    private Integer period;
    private String discountType;
    private String periodType;
    private String tariffSpecialType;
    private Integer freeTrialPeriod;
    private FreeTrialPeriodType freeTrialPeriodType;
    private BigDecimal bonus;

    private List<ServiceResponseDto> services = new ArrayList<>();
}
