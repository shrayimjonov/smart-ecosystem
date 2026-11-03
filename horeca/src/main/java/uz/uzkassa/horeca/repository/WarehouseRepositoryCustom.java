package uz.uzkassa.horeca.repository;

import uz.uzkassa.horeca.domain.warehouse.Warehouse;
import uz.uzkassa.horeca.dto.base.ResultList;
import uz.uzkassa.horeca.filters.BaseFilter;

public interface WarehouseRepositoryCustom {
    ResultList<Warehouse> getResultList(BaseFilter filter);
}
