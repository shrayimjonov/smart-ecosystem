package uz.uzkassa.smartpossupply.service.kpi;

import uz.uzkassa.smartpossupply.domain.kpi.KPI;
import uz.uzkassa.smartpossupply.enums.KPIStatus;
import uz.uzkassa.smartpossupply.enums.KPIType;

public interface KPIServiceStrategy<T, E, U> {
    KPIType getType();

    Long create(T dto);

    Long update(Long id, T dto);

    U toDTO(E entity);

    void validationUpdateStatus(KPI kpi, KPIStatus status);
}
