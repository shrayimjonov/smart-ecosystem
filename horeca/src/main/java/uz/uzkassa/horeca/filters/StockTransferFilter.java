package uz.uzkassa.horeca.filters;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class StockTransferFilter extends BaseFilter {

    Long fromWarehouseId;

    Long toWarehouseId;

    Long toBranchId;
}
