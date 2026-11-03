package uz.uzkassa.horeca.dto.companysettings;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import uz.uzkassa.horeca.dto.base.CommonDTO;
import uz.uzkassa.horeca.dto.company.CompanyCommonDTO;

import java.io.Serial;
import java.io.Serializable;
import java.math.BigDecimal;


@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
@NoArgsConstructor
public class CompanySettingsDetailDTO implements Serializable {

    @Serial
    private static final long serialVersionUID = 6556542222L;

    Long id;

    BigDecimal feePercent;

    CommonDTO branch;

    CompanyCommonDTO company;
}
