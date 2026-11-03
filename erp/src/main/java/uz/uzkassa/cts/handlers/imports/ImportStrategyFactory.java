package uz.uzkassa.cts.handlers.imports;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import uz.uzkassa.cts.enums.EntityTypeEnum;
import uz.uzkassa.cts.web.rest.errors.BadRequestException;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Component
public class ImportStrategyFactory {

    private final Map<EntityTypeEnum, ImportStrategy> strategyMap = new HashMap<>();

    @Autowired
    public ImportStrategyFactory(List<ImportStrategy> strategies) {
        strategies.forEach(strategy -> strategyMap.put(strategy.getEntityType(), strategy));
    }

    public ImportStrategy getStrategy(EntityTypeEnum entityType) {
        return Optional.ofNullable(strategyMap.get(entityType))
                .orElseThrow(() -> new BadRequestException("Invalid entity type"));
    }
}
