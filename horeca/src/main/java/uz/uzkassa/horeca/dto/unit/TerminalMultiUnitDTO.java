package uz.uzkassa.horeca.dto.unit;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import uz.uzkassa.horeca.dto.base.CommonDTO;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * Powered by: Shuxratjon Rayimjonov
 * Date: 16.08.2024
 */

@Getter
@Setter
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class TerminalMultiUnitDTO implements Serializable {

    Long id;

    Double weight;

    BigDecimal price;

    CommonDTO unit;

    CommonDTO techCard;
}
