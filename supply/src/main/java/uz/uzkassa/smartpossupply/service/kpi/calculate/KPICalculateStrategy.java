package uz.uzkassa.smartpossupply.service.kpi.calculate;

import uz.uzkassa.smartpossupply.dto.base.ResultList;
import uz.uzkassa.smartpossupply.enums.KPIType;

public interface KPICalculateStrategy<F, R, T> {
    KPIType getType();

    ResultList<R> calculate(F filter);

    T calculateTotal(F filter);
}
