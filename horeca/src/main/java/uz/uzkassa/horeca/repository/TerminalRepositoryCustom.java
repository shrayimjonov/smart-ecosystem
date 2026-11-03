package uz.uzkassa.horeca.repository;

import uz.uzkassa.horeca.domain.Terminal;
import uz.uzkassa.horeca.dto.base.ResultList;
import uz.uzkassa.horeca.filters.BaseFilter;

/**
 * Powered by: Shuxratjon Rayimjonov
 * Date: 10.05.2024
 */
public interface TerminalRepositoryCustom {
    ResultList<Terminal> getResultList(BaseFilter filter);
}
