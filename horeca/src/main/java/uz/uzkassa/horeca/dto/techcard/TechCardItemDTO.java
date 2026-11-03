package uz.uzkassa.horeca.dto.techcard;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import uz.uzkassa.horeca.enums.CookingType;

import java.io.Serial;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;
import java.util.Set;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class TechCardItemDTO implements Serializable {

    @Serial
    private static final long serialVersionUID = 89538L;

    Long id;

    Long ingredientId;

    List<CookingType> cookingTypes;

    BigDecimal gross;

    BigDecimal net;

    BigDecimal inKg;

    Long unitId;
}
