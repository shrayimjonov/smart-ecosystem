package uz.uzkassa.silen.repository.pg;

import org.springframework.data.domain.Page;
import uz.uzkassa.silen.domain.StoreIn;
import uz.uzkassa.silen.dto.TopSelectItem;
import uz.uzkassa.silen.dto.filter.Filter;
import uz.uzkassa.silen.dto.filter.StoreInFilter;

import java.util.List;

public interface StoreInRepositoryCustom{
    Page<StoreIn> findAllByFilter(StoreInFilter filter);

    List<TopSelectItem> topStoreIn(Filter filter);
}
