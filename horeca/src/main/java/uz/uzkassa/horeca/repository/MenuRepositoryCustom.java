package uz.uzkassa.horeca.repository;

import org.springframework.data.domain.Page;
import uz.uzkassa.horeca.domain.menu.Menu;
import uz.uzkassa.horeca.dto.base.ResultList;
import uz.uzkassa.horeca.filters.BaseFilter;

public interface MenuRepositoryCustom {
    Page<Menu> getResultList(BaseFilter filter);
}
