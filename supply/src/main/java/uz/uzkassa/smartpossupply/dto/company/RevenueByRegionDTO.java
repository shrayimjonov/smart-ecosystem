package uz.uzkassa.smartpossupply.dto.company;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

import java.math.BigDecimal;

@Setter
@Getter
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class RevenueByRegionDTO {

    Long regionId;

    BigDecimal amount;

    String name;

    String chartCode;

    public RevenueByRegionDTO(Long regionId, BigDecimal amount, String name, String chartCode) {
        this.regionId = regionId;
        this.amount = amount;
        this.name = name;
        this.chartCode = chartCode;
    }
}
