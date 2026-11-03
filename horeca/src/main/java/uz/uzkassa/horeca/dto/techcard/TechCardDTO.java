package uz.uzkassa.horeca.dto.techcard;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

import java.io.Serial;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class TechCardDTO implements Serializable {

    @Serial
    private static final long serialVersionUID = 3849583L;

    Long id;

    Long productId;

    String description;

    BigDecimal price;

    Long unitId;

    Double size;

    List<TechCardItemDTO> items;
}
