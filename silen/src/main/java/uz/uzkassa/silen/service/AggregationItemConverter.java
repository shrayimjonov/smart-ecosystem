package uz.uzkassa.silen.service;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.stereotype.Component;
import uz.uzkassa.silen.domain.mongo.Mark;
import uz.uzkassa.silen.enumeration.AggregationStatus;
import uz.uzkassa.silen.enumeration.AggregationType;
import uz.uzkassa.silen.integration.turon.AggregationItem;
import uz.uzkassa.silen.repository.mongo.MarkRepository;

import java.util.Collection;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

@Slf4j
@Component
public class AggregationItemConverter implements Function<ConvertableToAggregationItem, AggregationItem> {
    private final MarkRepository markRepository;

    public AggregationItemConverter(MarkRepository markRepository) {
        this.markRepository = markRepository;
    }

    @Override
    public AggregationItem apply(ConvertableToAggregationItem aggregation) {
        if (AggregationStatus.CREATED.equals(aggregation.getStatus())) {
            log.error("Aggregation {} status is CREATED", aggregation.getCode());
            return null;
        }
        final List<Mark> marks = markRepository.findAllByParentCode(aggregation.getCode());
        if (CollectionUtils.isEmpty(marks)) {
            log.error("Aggregation marks not found {}", aggregation.getCode());
            return null;
        }

        AggregationItem aggregationItem = new AggregationItem();
        aggregationItem.setAggregatedItemsCount(aggregation.getCapacity());
        aggregationItem.setAggregationUnitCapacity(aggregation.getCapacity());
        aggregationItem.setAggregationType(AggregationType.AGGREGATION);
        aggregationItem.setUnitSerialNumber(aggregation.getCode());
        aggregationItem.setSntins(marksToCode(marks));
        return aggregationItem;
    }

    protected Collection<String> marksToCode(final List<Mark> marks) {
        return marks.stream().map(Mark::getCode).collect(Collectors.toSet());
    }
}
