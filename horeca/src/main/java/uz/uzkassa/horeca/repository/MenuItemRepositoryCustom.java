package uz.uzkassa.horeca.repository;

import uz.uzkassa.horeca.domain.menu.MenuItem;
import uz.uzkassa.horeca.domain.unit.MultiUnit;
import uz.uzkassa.horeca.dto.base.ResultList;
import uz.uzkassa.horeca.filters.MenuItemFilter;

/**
 * Powered by: Shuxratjon Rayimjonov
 * Date: 07.05.2024
 */
public interface MenuItemRepositoryCustom {

    ResultList<MenuItem> getResultList(MenuItemFilter filter);

    ResultList<MultiUnit> getResultListForTerminal(MenuItemFilter filter);
}
