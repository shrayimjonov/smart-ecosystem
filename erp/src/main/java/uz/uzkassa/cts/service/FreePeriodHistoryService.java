package uz.uzkassa.cts.service;

import org.springframework.data.domain.Page;
import uz.uzkassa.cts.domain.FreePeriodHistory;
import uz.uzkassa.cts.dto.freeperiodhistory.response.FreePeriodHistoryResponseDto;
import uz.uzkassa.cts.filter.BaseFilter;

public interface FreePeriodHistoryService extends BaseService<FreePeriodHistory, Long>{
    Page<FreePeriodHistoryResponseDto> list(BaseFilter filter);
}
