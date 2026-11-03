package uz.uzkassa.smartpossupply.service.kpi;

import org.springframework.stereotype.Component;
import uz.uzkassa.smartpossupply.domain.kpi.KPI;
import uz.uzkassa.smartpossupply.dto.kpi.KPIBaseDTO;
import uz.uzkassa.smartpossupply.dto.kpi.detail.KPIDetailDTO;
import uz.uzkassa.smartpossupply.enums.KPIType;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Component
public class KpiServiceFactoryMap {
    private final Map<KPIType, KPIServiceStrategy<?, ?, ?>> strategies;

    public KpiServiceFactoryMap(List<KPIServiceStrategy<?, ?, ?>> strategies) {
        this.strategies = strategies.stream()
                .collect(Collectors.toMap(
                        KPIServiceStrategy::getType,
                        Function.identity()
                ));
    }

    @SuppressWarnings("unchecked") // centralize the cast here
    public <T extends KPIBaseDTO, E extends KPI, U extends KPIDetailDTO> KPIServiceStrategy<T, E, U> getService(KPIType type) {
        KPIServiceStrategy<?, ?, ?> strategy = strategies.get(type);
        if (strategy == null) {
            throw new IllegalArgumentException("No strategy found for type: " + type);
        }
        return (KPIServiceStrategy<T, E, U>) strategy;
    }
}