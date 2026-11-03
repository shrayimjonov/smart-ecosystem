package uz.uzkassa.cts.repository.custom;

import org.springframework.stereotype.Repository;
import uz.uzkassa.cts.domain.hrms.Department;
import uz.uzkassa.cts.dto.base.ResultList;
import uz.uzkassa.cts.filter.BaseFilter;

@Repository
public interface DepartmentRepositoryCustom {

    ResultList<Department> getResultList(BaseFilter filter);
}
