package uz.uzkassa.silen.repository.mongo;

import org.springframework.data.domain.Page;
import uz.uzkassa.silen.dto.filter.MongoFilter;

import java.math.BigDecimal;
import java.util.List;

public interface MarkRepositoryCustom<T> {
    Page<T> findAllByFilter(MongoFilter filter);

    BigDecimal findAllByFilterStats(MongoFilter filter);

    List<T> getPartyAggregations(String partyAggregationId);

}
