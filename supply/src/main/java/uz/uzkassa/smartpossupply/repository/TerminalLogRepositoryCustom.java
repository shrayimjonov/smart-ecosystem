package uz.uzkassa.smartpossupply.repository;

import uz.uzkassa.smartpossupply.domain.TerminalLog;
import uz.uzkassa.smartpossupply.dto.base.ResultList;
import uz.uzkassa.smartpossupply.filters.BaseFilter;

public interface TerminalLogRepositoryCustom {
    ResultList<TerminalLog> getResultList(BaseFilter filter);
}
