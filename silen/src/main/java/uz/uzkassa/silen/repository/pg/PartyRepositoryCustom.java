package uz.uzkassa.silen.repository.pg;

import org.springframework.data.domain.Page;
import uz.uzkassa.silen.domain.Party;
import uz.uzkassa.silen.dto.TopSelectItem;
import uz.uzkassa.silen.dto.filter.Filter;
import uz.uzkassa.silen.dto.filter.MongoFilter;

import java.util.List;

public interface PartyRepositoryCustom {
    Page<Party> findAllByFilter(MongoFilter filter);

    List<TopSelectItem> topOrders(Filter filter);
}
