package uz.uzkassa.silen.dto.warehouse;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class WarehouseOperationStatsDTO {

    BigDecimal amount;
    BigDecimal block;
    BigDecimal box;
    BigDecimal pallet;

}
