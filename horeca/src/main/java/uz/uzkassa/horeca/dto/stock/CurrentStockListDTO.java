package uz.uzkassa.horeca.dto.stock;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import uz.uzkassa.horeca.dto.base.CommonDTO;
import uz.uzkassa.horeca.dto.unit.MultiUnitListDTO;
import uz.uzkassa.horeca.enums.CommonStatus;
import uz.uzkassa.horeca.enums.CurrentStockStatus;

import java.io.Serial;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;

/**
 * Powered by: Shuxratjon Rayimjonov
 * Date: 10.11.2024
 */
@Getter
@Setter
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CurrentStockListDTO implements Serializable {

    @Serial
    private static final long serialVersionUID = 8389653L;

    Long id;

    CommonDTO menuItem;

    CommonDTO department;

    BigDecimal qty;

    BigDecimal soldQty;

    CommonDTO unit;

    List<MultiUnitListDTO> units;

    boolean recurring;

    boolean transferPreviousQty;

    CurrentStockStatus status;
}
