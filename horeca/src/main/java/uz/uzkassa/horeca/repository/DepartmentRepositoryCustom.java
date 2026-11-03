package uz.uzkassa.horeca.repository;

import uz.uzkassa.horeca.domain.Department;
import uz.uzkassa.horeca.dto.base.ResultList;
import uz.uzkassa.horeca.filters.BaseFilter;

public interface DepartmentRepositoryCustom {
    ResultList<Department> getResultList(BaseFilter filter);
}
