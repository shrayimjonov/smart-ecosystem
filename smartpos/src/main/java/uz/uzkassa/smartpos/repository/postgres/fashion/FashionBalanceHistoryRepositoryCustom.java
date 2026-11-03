package uz.uzkassa.smartpos.repository.postgres.fashion;

import org.springframework.data.domain.Page;
import uz.uzkassa.smartpos.dto.fashion.balance.FashionBalanceHistoryDto;
import uz.uzkassa.smartpos.dto.fashion.balance.FashionBalanceHistoryStatisticsDto;
import uz.uzkassa.smartpos.dto.fashion.balance.FashionColorGroupHistoryDto;
import uz.uzkassa.smartpos.filter.FashionBalanceHistoryFilter;

import java.util.List;

public interface FashionBalanceHistoryRepositoryCustom {
    Page<FashionBalanceHistoryDto> findAllByFilter(FashionBalanceHistoryFilter filter);

    List<FashionColorGroupHistoryDto> findAllColorGroupByFilter(FashionBalanceHistoryFilter filter);

    FashionBalanceHistoryStatisticsDto getStatisticsByFilter(FashionBalanceHistoryFilter filter);
}
