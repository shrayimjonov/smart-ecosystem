package uz.uzkassa.silen.dto.warehouse;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import uz.uzkassa.silen.enumeration.WarehouseOperation;

import java.io.Serializable;
import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
public class WarehouseSelectItem implements Serializable {
    private String id;
    private String name;
    private String operation;
    private BigDecimal amount;

    public WarehouseSelectItem(String id, String name, String operation, BigDecimal  amount) {
        this.id = id;
        this.name = name;
        this.operation = WarehouseOperation.valueOf(operation).getNameRu();
        this.amount = amount;
    }
}
