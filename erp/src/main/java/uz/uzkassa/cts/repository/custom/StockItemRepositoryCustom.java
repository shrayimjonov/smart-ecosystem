package uz.uzkassa.cts.repository.custom;

import org.springframework.stereotype.Repository;
import uz.uzkassa.cts.dto.base.CommonDTO;
import uz.uzkassa.cts.filter.StockFilter;

import java.util.List;

@Repository
public interface StockItemRepositoryCustom {

    List<CommonDTO> lookUp(StockFilter filter);
}
