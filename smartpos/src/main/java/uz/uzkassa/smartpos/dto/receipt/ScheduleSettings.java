package uz.uzkassa.smartpos.dto.receipt;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import uz.uzkassa.smartpos.domain.postgres.catalog.CreditSettings;

import java.math.BigDecimal;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ScheduleSettings extends CreditSettings {
    private BigDecimal nds;
}
