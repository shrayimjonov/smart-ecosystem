package uz.uzkassa.silen.repository.pg;

import org.springframework.data.domain.Page;
import uz.uzkassa.silen.domain.Dropout;
import uz.uzkassa.silen.dto.DropoutStatsDTO;
import uz.uzkassa.silen.dto.TopSelectItem;
import uz.uzkassa.silen.dto.filter.DropoutFilter;
import uz.uzkassa.silen.dto.filter.Filter;

import java.util.List;

public interface DropoutRepositoryCustom {
    Page<Dropout> findAllByFilter(DropoutFilter filter);

    DropoutStatsDTO findAllByFilterStats(DropoutFilter filter);

    List<TopSelectItem> topDropout(Filter filter);
}
