package uz.uzkassa.smartpossupply.repository;

import org.springframework.stereotype.Repository;
import uz.uzkassa.smartpossupply.domain.warehouse.Warehouse;
import uz.uzkassa.smartpossupply.dto.base.ResultList;
import uz.uzkassa.smartpossupply.filters.BaseFilter;

import java.util.List;

@Repository
public interface WarehouseRepositoryCustom {
    ResultList<Warehouse> getResultList(BaseFilter filter);

    List<Warehouse> lookUp(BaseFilter filter);

    boolean checkQtyInWarehouse(Long warehouseId);
}
