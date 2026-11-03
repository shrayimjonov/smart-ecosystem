package uz.uzkassa.horeca.repository;

import uz.uzkassa.horeca.domain.warehouse.WarehouseItem;
import uz.uzkassa.horeca.dto.base.ResultList;
import uz.uzkassa.horeca.filters.BaseFilter;

import java.math.BigDecimal;

/**
 * Powered by: Shuxratjon Rayimjonov
 * Date: 20.12.2023
 */
public interface WarehouseItemRepositoryCustom {

    ResultList<WarehouseItem> getReport(BaseFilter filter);

    BigDecimal getReportTotalQty(BaseFilter filter);

}
