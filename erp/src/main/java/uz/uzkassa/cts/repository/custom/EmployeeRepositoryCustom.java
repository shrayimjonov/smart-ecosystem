package uz.uzkassa.cts.repository.custom;

import org.springframework.stereotype.Repository;
import uz.uzkassa.cts.domain.User;
import uz.uzkassa.cts.dto.base.ResultList;
import uz.uzkassa.cts.dto.employee.EmployeeListDTO;
import uz.uzkassa.cts.dto.user.EmployeeListByBranchDTO;
import uz.uzkassa.cts.filter.BaseFilter;
import uz.uzkassa.cts.filter.EmployeeFilter;

import java.util.List;

@Repository
public interface EmployeeRepositoryCustom {

    ResultList<User> getResultList(EmployeeFilter filter);
}
