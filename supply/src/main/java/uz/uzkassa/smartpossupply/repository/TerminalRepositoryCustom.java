package uz.uzkassa.smartpossupply.repository;


import uz.uzkassa.smartpossupply.domain.Terminal;
import uz.uzkassa.smartpossupply.dto.base.ResultList;
import uz.uzkassa.smartpossupply.dto.branch.CommonStatisticsDTO;
import uz.uzkassa.smartpossupply.filters.TerminalFilter;

/**
 * Powered by: Shuxratjon Rayimjonov
 * Date: 10.05.2024
 */
public interface TerminalRepositoryCustom {
    ResultList<Terminal> getResultList(TerminalFilter filter);

    CommonStatisticsDTO getStatistics(TerminalFilter filter);
}
