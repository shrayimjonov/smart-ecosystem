package uz.uzkassa.horeca.dto.companysettings;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

import java.io.Serial;
import java.io.Serializable;
import java.math.BigDecimal;


@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
@NoArgsConstructor
public class CompanySettingsDTO implements Serializable {

    @Serial
    private static final long serialVersionUID = 3433432222L;

    BigDecimal feePercent;

    Long branchId;
}
