package uz.uzkassa.cts.repository.custom;

import org.springframework.stereotype.Repository;
import uz.uzkassa.cts.domain.History;
import uz.uzkassa.cts.dto.base.ResultList;
import uz.uzkassa.cts.filter.HistoryListFilter;

@Repository
public interface HistoryRepositoryCustom {

    ResultList<History> getListResult(HistoryListFilter filter);
}
