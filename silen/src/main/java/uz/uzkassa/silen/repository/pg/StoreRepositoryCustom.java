package uz.uzkassa.silen.repository.pg;

import org.springframework.data.domain.Page;
import uz.uzkassa.silen.domain.Store;
import uz.uzkassa.silen.dto.filter.StoreFilter;

public interface StoreRepositoryCustom {
    Page<Store> findAllByFilter(StoreFilter filter);

}
