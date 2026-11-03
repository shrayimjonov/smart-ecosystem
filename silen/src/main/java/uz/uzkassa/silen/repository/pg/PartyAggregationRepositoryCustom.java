package uz.uzkassa.silen.repository.pg;

import org.springframework.data.domain.Page;
import uz.uzkassa.silen.domain.PartyAggregation;
import uz.uzkassa.silen.dto.filter.PartyAggregationFilter;

public interface PartyAggregationRepositoryCustom {

    Page<PartyAggregation> findAllByFilter(PartyAggregationFilter filter);
}
