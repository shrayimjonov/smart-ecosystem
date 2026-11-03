package uz.uzkassa.smartpossupply.repository;


import uz.uzkassa.smartpossupply.domain.SystemUpgrade;
import uz.uzkassa.smartpossupply.dto.base.ResultList;
import uz.uzkassa.smartpossupply.filters.SystemUpgradeFilter;

/**
 * Powered by: Shuxratjon Rayimjonov
 * Date: 26.11.2024
 */
public interface SystemUpgradeRepositoryCustom {
    ResultList<SystemUpgrade> getResultList(SystemUpgradeFilter filter);
}
