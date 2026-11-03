package uz.uzkassa.horeca.dto.cateringsettings;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import uz.uzkassa.horeca.dto.base.CommonDTO;
import uz.uzkassa.horeca.enums.CommonStatus;

import java.io.Serial;
import java.io.Serializable;
import java.math.BigDecimal;

import static lombok.AccessLevel.PRIVATE;

@Setter
@Getter
@FieldDefaults(level = PRIVATE)
@NoArgsConstructor
public class HallFeeListDTO extends CommonDTO implements Serializable {

    @Serial
    private static final long serialVersionUID = 45839458L;

    BigDecimal feePercent;
}
