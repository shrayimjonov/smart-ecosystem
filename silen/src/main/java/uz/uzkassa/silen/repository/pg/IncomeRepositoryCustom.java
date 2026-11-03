package uz.uzkassa.silen.repository.pg;

import org.springframework.data.domain.Page;
import uz.uzkassa.silen.domain.Income;
import uz.uzkassa.silen.dto.filter.WarehouseFilter;

public interface IncomeRepositoryCustom {
    Page<Income> findAllByFilter(WarehouseFilter filter);
}
