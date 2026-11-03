package uz.uzkassa.smartpossupply.filters;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class StockTransferFilter extends BaseFilter {

    Long fromWarehouseId;

    Long toWarehouseId;

    Long fromBranchId;

    Long toBranchId;

    Long expeditorId;
}
