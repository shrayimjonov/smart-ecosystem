package uz.uzkassa.smartpossupply.repository.kpi;

import org.springframework.stereotype.Repository;
import uz.uzkassa.smartpossupply.dto.kpi.KPISalesPlanDTO;

@Repository
public interface KPISalesPlanRepositoryCustom {
    boolean existByFields(KPISalesPlanDTO kpiSalesPlanDTO);
}
