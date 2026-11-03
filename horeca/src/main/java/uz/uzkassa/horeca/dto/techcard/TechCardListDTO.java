package uz.uzkassa.horeca.dto.techcard;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import uz.uzkassa.horeca.dto.base.CommonDTO;

import java.io.Serial;
import java.io.Serializable;
import java.math.BigDecimal;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class TechCardListDTO implements Serializable {

    @Serial
    private static final long serialVersionUID = 38435459583L;

    Long id;

    CommonDTO product;

    String description;

    BigDecimal price;
}
