package uz.uzkassa.horeca.repository;

import uz.uzkassa.horeca.domain.SystemUpgrade;
import uz.uzkassa.horeca.dto.base.ResultList;
import uz.uzkassa.horeca.filters.SystemUpgradeFilter;

/**
 * Powered by: Shuxratjon Rayimjonov
 * Date: 03.10.2024
 */
public interface SystemUpgradeRepositoryCustom {
    ResultList<SystemUpgrade> getResultList(SystemUpgradeFilter filter);
}
