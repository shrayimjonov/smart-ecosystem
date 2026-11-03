package uz.uzkassa.horeca.dto.stock;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

import java.io.Serial;
import java.io.Serializable;
import java.math.BigDecimal;

/**
 * Powered by: Shuxratjon Rayimjonov
 * Date: 10.11.2024
 */
@Getter
@Setter
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CurrentStockDTO implements Serializable {

    @Serial
    private static final long serialVersionUID = 8389653L;

    Long menuItemId;

    BigDecimal qty;

    boolean recurring;

    boolean transferPreviousQty;

    public CurrentStockDTO(Long menuItemId) {
        this.menuItemId = menuItemId;
    }
}
