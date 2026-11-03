package uz.uzkassa.horeca.dto.techcard;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import uz.uzkassa.horeca.dto.base.CommonCodeDTO;
import uz.uzkassa.horeca.dto.base.CommonDTO;

import java.io.Serial;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class TechCardDetailDTO implements Serializable {

    @Serial
    private static final long serialVersionUID = 2343453L;

    Long id;

    CommonDTO product;

    String description;

    BigDecimal price;

    Double size;

    CommonCodeDTO unit;

    List<TechCardItemDetailDTO> items;
}
