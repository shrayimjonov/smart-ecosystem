package uz.uzkassa.smartpossupply.repository;

import org.springframework.stereotype.Repository;
import uz.uzkassa.smartpossupply.dto.base.ResultList;
import uz.uzkassa.smartpossupply.dto.product.WarehouseExpeditorMapDTO;
import uz.uzkassa.smartpossupply.dto.warehouseexpeditor.WarehouseExpeditorStatisticsDTO;
import uz.uzkassa.smartpossupply.filters.BaseFilter;

/**
 * Powered by: Shuxratjon Rayimjonov
 * Date: 29.11.2024
 */
@Repository
public interface WarehouseExpeditorRepositoryCustom {

    WarehouseExpeditorStatisticsDTO getStatistics(BaseFilter filter);

    ResultList<WarehouseExpeditorMapDTO> getList(BaseFilter filter);

    ResultList<WarehouseExpeditorMapDTO> getCarList(BaseFilter filter);
}
