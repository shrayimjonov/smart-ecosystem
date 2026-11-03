package uz.uzkassa.smartpos.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;
import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
public class UnitSelectItem extends SelectItem implements Serializable {
    private boolean isBase;
    private Double coefficient;
    private BigDecimal balance;

    public UnitSelectItem(Long id, String name, boolean isBase) {
        super(id, name);
        this.isBase = isBase;
    }
}
