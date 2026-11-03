package uz.uzkassa.horeca.dto.unit;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import uz.uzkassa.horeca.enums.EntityTypeEnum;

import java.io.Serializable;
import java.math.BigDecimal;


@Getter
@Setter
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class MultiUnitDTO implements Serializable {

    Long id;

    Double weight;

    BigDecimal price;

    Long menuItemId;

    Long techCardId;
}
