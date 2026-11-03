package uz.uzkassa.cts.filter;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class StockTransferFilter extends BaseFilter {

    Long fromWarehouseId;

    Long toWarehouseId;

    Long fromBranchId;

    Long toBranchId;
}
