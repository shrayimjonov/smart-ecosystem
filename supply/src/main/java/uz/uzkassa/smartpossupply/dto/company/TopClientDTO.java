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
public class TopClientDTO {

    BigDecimal percentage;

    BigDecimal amount;

    Long companyId;

    String companyName;

    public TopClientDTO(BigDecimal percentage, BigDecimal amount, Long companyId, String companyName) {
        this.percentage = percentage;
        this.amount = amount;
        this.companyId = companyId;
        this.companyName = companyName;
    }
}
