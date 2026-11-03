package uz.uzkassa.smartpossupply.repository.kpi;

import org.springframework.stereotype.Repository;
import uz.uzkassa.smartpossupply.domain.kpi.KPI;
import uz.uzkassa.smartpossupply.enums.KPIStatus;

@Repository
public interface KPINewClientRepositoryCustom {
    boolean updateStatus(KPI kpi, KPIStatus status);
}
