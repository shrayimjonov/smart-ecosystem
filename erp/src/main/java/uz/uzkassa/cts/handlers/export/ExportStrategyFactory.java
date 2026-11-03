package uz.uzkassa.cts.handlers.export;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import uz.uzkassa.cts.enums.EntityTypeEnum;
import uz.uzkassa.cts.web.rest.errors.BadRequestException;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Component
public class ExportStrategyFactory {

    private final Map<EntityTypeEnum, ExportStrategy> strategyMap = new HashMap<>();

    @Autowired
    public ExportStrategyFactory(List<ExportStrategy> strategies) {
        strategies.forEach(strategy -> strategyMap.put(strategy.getEntityType(), strategy));
    }

    public ExportStrategy getStrategy(EntityTypeEnum entityType) {
        return Optional.ofNullable(strategyMap.get(entityType))
                .orElseThrow(() -> new BadRequestException("Invalid entity type"));
    }
}
