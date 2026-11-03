package uz.uzkassa.smartpossupply.repository.kpi;

import uz.uzkassa.smartpossupply.domain.kpi.KPI;
import uz.uzkassa.smartpossupply.dto.base.ResultList;
import uz.uzkassa.smartpossupply.filters.KPIFilter;

public interface KPIRepositoryCustom {
    ResultList<KPI> getResultList(KPIFilter kpiFilter);
}
