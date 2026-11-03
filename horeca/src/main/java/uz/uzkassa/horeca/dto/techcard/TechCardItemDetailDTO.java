package uz.uzkassa.horeca.dto.techcard;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import uz.uzkassa.horeca.dto.base.CommonDTO;
import uz.uzkassa.horeca.enums.CookingType;

import java.io.Serial;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class TechCardItemDetailDTO implements Serializable {

    @Serial
    private static final long serialVersionUID = 54654654L;

    Long id;

    CommonDTO ingredient;

    List<CookingType> cookingTypes;

    BigDecimal gross;

    BigDecimal net;

    BigDecimal inKg;

    CommonDTO unit;
}
