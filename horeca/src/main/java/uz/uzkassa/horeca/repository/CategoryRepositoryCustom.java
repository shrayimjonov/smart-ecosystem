package uz.uzkassa.horeca.repository;

import uz.uzkassa.horeca.domain.Category;
import uz.uzkassa.horeca.dto.base.ResultList;
import uz.uzkassa.horeca.filters.BaseFilter;

public interface CategoryRepositoryCustom {
    ResultList<Category> getResultList(BaseFilter filter);
}
