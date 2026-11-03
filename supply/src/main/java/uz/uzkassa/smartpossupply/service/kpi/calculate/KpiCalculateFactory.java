package uz.uzkassa.smartpossupply.service.kpi.calculate;

import org.springframework.stereotype.Component;
import uz.uzkassa.smartpossupply.dto.kpi.KPICalculateDTO;
import uz.uzkassa.smartpossupply.dto.kpi.KPICalculateTotalDTO;
import uz.uzkassa.smartpossupply.enums.KPIType;
import uz.uzkassa.smartpossupply.filters.KPIFilter;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Component
public class KpiCalculateFactory {
    private final Map<KPIType, KPICalculateStrategy<?, ?, ?>> strategies;

    public KpiCalculateFactory(List<KPICalculateStrategy<?, ?, ?>> strategies) {
        this.strategies = strategies.stream()
                .collect(Collectors.toMap(
                        KPICalculateStrategy::getType,
                        Function.identity()
                ));
    }

    @SuppressWarnings("unchecked") // centralize the cast here
    public <F extends KPIFilter, R extends KPICalculateDTO, T extends KPICalculateTotalDTO> KPICalculateStrategy<F, R, T> getService(KPIType type) {
        KPICalculateStrategy<?, ?, ?> strategy = strategies.get(type);
        if (strategy == null) {
            throw new IllegalArgumentException("No strategy found for type: " + type);
        }
        return (KPICalculateStrategy<F, R, T>) strategy;
    }
}