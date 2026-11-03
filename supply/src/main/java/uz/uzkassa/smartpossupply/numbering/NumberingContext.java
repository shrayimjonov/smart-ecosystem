package uz.uzkassa.smartpossupply.numbering;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import uz.uzkassa.smartpossupply.enums.Prefix;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class NumberingContext {

    private final Map<Prefix, NumberingStrategy> strategyMap;

    @Autowired
    public NumberingContext(List<NumberingStrategy> strategies) {
        this.strategyMap = new HashMap<>();
        strategies.forEach(strategy -> strategyMap.put(strategy.getPrefix(), strategy));
    }

    public Map<String, String> generateNumber(Prefix prefix, Long companyId) {
        NumberingStrategy strategy = strategyMap.get(prefix);
        if (strategy == null) {
            throw new IllegalArgumentException("No numbering strategy found for prefix: " + prefix);
        }
        return strategy.generateNumber(companyId);
    }
}
